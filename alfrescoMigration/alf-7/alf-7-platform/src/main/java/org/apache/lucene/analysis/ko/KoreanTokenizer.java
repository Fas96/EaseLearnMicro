package org.apache.lucene.analysis.ko;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ko.morph.MorphException;
import org.apache.lucene.analysis.ko.utils.SyllableUtil;

public final class KoreanTokenizer extends Tokenizer {

    private int offset = 0, bufferIndex = 0, dataLen = 0, finalOffset = 0;
    private static final int MAX_WORD_LEN = 255;
    private static final int IO_BUFFER_SIZE = 6;

    private static final int DEFAULT_BUFFER_SIZE = 256;

    private final CharacterBuffer ioBuffer = new CharacterBuffer(new char[IO_BUFFER_SIZE], 0, 0);
    
    private static Map<Integer,Integer> pairmap = new HashMap<Integer,Integer>();

    static {
        pairmap.put(34,34);// ""
        pairmap.put(39,39);// ''
        pairmap.put(40,41);// ()
        pairmap.put(60,62);// <>
        pairmap.put(91,93);// []
        pairmap.put(123,125);// {}
        pairmap.put(65288,65289);// ‘’
        pairmap.put(8216,8217);// ‘’
        pairmap.put(8220,8221);// “”
    }

    private List<Integer> pairstack = new ArrayList<Integer>();

    public static final String TYPE_KOREAN = "korean";
    public static final String TYPE_WORD = "word";
    public static final String TYPE_SIMBOL = "symbol";

    private boolean done;
    
    public KoreanTokenizer(Reader input) {
    	super(input);
    }

    public Token next(final Token reusableToken) throws IOException {
   	 	assert reusableToken != null;
        char[] buffer = reusableToken.termBuffer();

        int length = 0;
        int start = -1; // this variable is always initialized
        int end = -1;
        int pos = reusableToken.getPositionIncrement();
 
        while (true) {
            if (bufferIndex >= dataLen) {
                offset += dataLen;
                fill(ioBuffer, input); // read supplementary char aware with CharacterUtils
                if (ioBuffer.getLength() == 0) {
                    dataLen = 0; // so next offset += dataLen won't decrement offset
                    if (length > 0) {
                        break;
                    } else {
                        finalOffset = offset;
                        return null;
                    }
                }
                dataLen = ioBuffer.getLength();
                bufferIndex = 0;
            }

            // use CharacterUtils here to support < 3.1 UTF-16 code unit behavior if the char based methods are gone
            final int c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());
            final int charCount = Character.charCount(c);
            bufferIndex += charCount;

            char inspect_c = (char)c;
            
            int closechar = getPairChar(c);
           
            if(closechar!=0 && 
            		(pairstack.isEmpty() || 
            				(!pairstack.isEmpty() && pairstack.get(0)!=c))) {
            	if(start==-1) {
            		start=offset + bufferIndex - charCount;
            		end=start;
            	}
            	end += charCount;
                length += Character.toChars(c, buffer, length); // buffer it
                pairstack.add(0,closechar);
                
                break; 
            } else if (isTokenChar(c) || 
            		(pairstack.size()>0 && pairstack.get(0)==c)) {               // if it's a token char
                if (length == 0) {                // start of token
                    assert start == -1;
                    start = offset + bufferIndex - charCount;
                    end = start;
                } else if (length >= buffer.length - 1) { // check if a supplementary could run out of bounds
                	buffer = reusableToken.resizeTermBuffer(2 + length); // make sure a supplementary fits in the buffer
                }
                end += charCount;
                length += Character.toChars(c, buffer, length); // buffer it
                
                // delimited close character

                
//                // check if next token is parenthesis.
                if(isDelimitPosition(length, c)) {
                    if(!pairstack.isEmpty() && pairstack.get(0)==c) {
                    	pairstack.remove(0);
                    }
                	if(bufferIndex < dataLen) break;
                }
                
                if(!pairstack.isEmpty() && pairstack.get(0)==c) {
                	pairstack.remove(0);
                }
                
                if (length >= MAX_WORD_LEN)
                    break; // buffer overflow! make sure to check for >= surrogate pair could break == test
            } else if (length > 0) {           // at non-Letter w/ chars
                break;
            }// return 'em
            
        }

        String type = TokenUtilities.getType(buffer, length);

        reusableToken.setTermBuffer(buffer, 0, buffer.length);
        reusableToken.setStartOffset(start);
        reusableToken.setEndOffset(end);
        reusableToken.setTermLength(length);
        reusableToken.setType(type);

        return reusableToken;
    }

	/**
	 * @return
	 */
	private boolean isDelimitPosition(int length, int c) {
		
		if(bufferIndex>=dataLen ||
				(length==1 && !pairstack.isEmpty() && pairstack.get(0)==c)) return true;
		
		int next_c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex, ioBuffer.getLength());
		if(isTokenChar(next_c)) return false;
		
		if(pairstack.size()==0) return true;
		
		int next_closechar = getPairChar(next_c);
		if(next_closechar!=0 && pairstack.get(0)!=next_closechar) 
			return true;
		
		int size = pairstack.size();
		if((ioBuffer.getLength()-bufferIndex)<size) size = ioBuffer.getLength()-bufferIndex;
		
		if(next_c!=pairstack.get(0)) return false; // if next character is not close parenthesis
		
		for(int i=1;i<size;i++) {
			next_c = Character.codePointAt(ioBuffer.getBuffer(), bufferIndex+i, ioBuffer.getLength());
			if(next_c!=pairstack.get(i)) return true;
		}

		
		try {
			int start = bufferIndex+size;
			int end = Math.min(ioBuffer.getLength(), start + 2);
			
			boolean hasParticle = false;
			for(int i=start;i<end;i++) {
				int space_c = Character.codePointAt(ioBuffer.getBuffer(), i, ioBuffer.getLength());
				
				if(space_c==32) { // 32 is space ascii code
					if(i==start)
						return true;
					else
						return false;
				}
				
				char[] feature =  SyllableUtil.getFeature((char)space_c);
				
				if(i==start && !(feature[SyllableUtil.IDX_JOSA1]=='1' || feature[SyllableUtil.IDX_EOMI1]=='1')) {
					return true;
				} else if(i==start+1 && !(feature[SyllableUtil.IDX_JOSA2]=='1' || feature[SyllableUtil.IDX_EOMI2]=='1')) {
					return true;
				} 
				
				hasParticle = true;
			}

			return !hasParticle;
			
		} catch (MorphException e) {
			throw new RuntimeException("Error occured while reading a josa");
		}

	}
    
    private boolean isTokenChar(int c) {
        if(Character.isLetterOrDigit(c) || isPreserveSymbol((char)c)) return true;
        return false;
    }

    private int getPairChar(int c) {
        Integer p = pairmap.get(c);
        return p==null ? 0 : p;
    }


    private boolean isPreserveSymbol(char c) {
        return (c=='#' || c=='+' || c=='-' || c=='/' || c=='·' || c == '&' || c == '_');
    }

//    @Override
//    public final void end() throws IOException {
//        super.end();
//        // set final offset
//        offsetAtt.setOffset(finalOffset, finalOffset);
//    }

    @Override
    public void reset() throws IOException {
        super.reset();
        bufferIndex = 0;
        offset = 0;
        dataLen = 0;
        finalOffset = 0;
//        ioBuffer.reset(); // make sure to reset the IO buffer!!
    }

    /** Convenience method which calls <code>fill(buffer, reader, buffer.buffer.length)</code>. */
    private boolean fill(CharacterBuffer buffer, Reader reader) throws IOException {
      return fill(buffer, reader, buffer.buffer.length);
    }

    private boolean fill(CharacterBuffer buffer, Reader reader, int numChars) throws IOException {
        assert buffer.buffer.length >= 2;
        if (numChars < 2 || numChars > buffer.buffer.length) {
          throw new IllegalArgumentException("numChars must be >= 2 and <= the buffer size");
        }
        final char[] charBuffer = buffer.buffer;
        buffer.offset = 0;
        final int offset;

        // Install the previously saved ending high surrogate:
        if (buffer.lastTrailingHighSurrogate != 0) {
          charBuffer[0] = buffer.lastTrailingHighSurrogate;
          buffer.lastTrailingHighSurrogate = 0;
          offset = 1;
        } else {
          offset = 0;
        }

        final int read = readFully(reader, charBuffer, offset, numChars - offset);

        buffer.length = offset + read;
        final boolean result = buffer.length == numChars;
        if (buffer.length < numChars) {
          // We failed to fill the buffer. Even if the last char is a high
          // surrogate, there is nothing we can do
          return result;
        }

        if (Character.isHighSurrogate(charBuffer[buffer.length - 1])) {
          buffer.lastTrailingHighSurrogate = charBuffer[--buffer.length];
        }
        return result;
    }

    private int readFully(Reader reader, char[] dest, int offset, int len) throws IOException {
	    int read = 0;
	    while (read < len) {
	      final int r = reader.read(dest, offset + read, len - read);
	      if (r == -1) {
	        break;
	      }
	      read += r;
	    }
	    return read;
   }
    
}
