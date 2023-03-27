package org.apache.lucene.analysis.ko;

import java.io.IOException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;

/**
 * Created by SooMyung(soomyung.lee@gmail.com) on 2014. 7. 30.
 */

public final class PunctuationDelimitFilter extends TokenFilter {

    private final LinkedList<Token> outQueue = new LinkedList<Token>();

	private boolean hasSplitedTerm = true;
	
	private boolean hasPunctation = true;
	
	private boolean hasConcatedTerm = false;
	
	private int minTermSize = 1;
    
    /**
     * Construct a token stream filtering the given input.
     *
     * @param input
     */
    protected PunctuationDelimitFilter(TokenStream input) {
        super(input);
    }

    public void setHasSplitedTerm(boolean has) {
    	this.hasSplitedTerm = has;
    }
    
    public void setHasPunctation(boolean has) {
    	this.hasPunctation = has;
    }
    
    public void setHasConcatedTerm(boolean has) {
    	this.hasConcatedTerm = has;
    }
    
    public void setMinTermSize(int size) {
    	this.minTermSize = size;
    }
    
    public final Token next(final Token reusableToken) throws IOException {
        assert reusableToken != null;
        
        if (!outQueue.isEmpty()) {
            return outQueue.removeFirst();
        }
        
        while(outQueue.isEmpty()) {
        	
            Token nextToken = input.next(reusableToken);

            if (nextToken == null) return null;
            
            int start = nextToken.startOffset();
            int posInc = nextToken.getPositionIncrement();
            
        	if (KoreanTokenizer.TYPE_SIMBOL.equals(nextToken.type())) 
        		return nextToken;
        	
            if(!containPunctuation(nextToken.term())) 
            	return nextToken;

            splitByPunctuation(nextToken.term(), start, posInc);

            if (!outQueue.isEmpty()) {
                return outQueue.removeFirst();
            }
        }
        
        return null;
    }
//    
//    @Override
//    public boolean incrementToken() throws IOException {
//
//        if (!outQueue.isEmpty()) {
//            restoreState(currentState);
//            setAttributesFromQueue(false);
//            return true;
//        }
//
//
//        
//        while (input.incrementToken()) {
//        	if (keywordAtt.isKeyword() || 
//        			KoreanTokenizer.TYPE_SIMBOL.equals(typeAtt.type())) return true;
//            if(!containPunctuation(termAtt.toString())) return true;
//
//            splitByPunctuation(termAtt.toString());
//
//            if (!outQueue.isEmpty()) {
//                setAttributesFromQueue(true);
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private void setAttributesFromQueue(boolean isFirst) {
//        final Token iw = outQueue.removeFirst();
//
//        if (isFirst && !outQueue.isEmpty()) {
////            termAtt.setEmpty();
//            currentState = captureState();
//        }
//
//        termAtt.setEmpty().append(iw.getTerm());
//        offsetAtt.setOffset(iw.getOffset(), iw.getEndOffset());
//        posIncrAtt.setPositionIncrement(iw.getIncrement());
//    }

    private void splitByPunctuation(String term, int startOffset, int posInc) {
        if(term.length()<2) return;
        StringBuffer sb = new StringBuffer();

        List<String> array = new ArrayList<String>();
        List<String> fullstr = new ArrayList<String>();
        
        boolean containChar = false;
        int skip = 0;
        for(int i=0;i<term.length();i++) {
            if(isPunctuation(term.charAt(i))) {
                if(sb.length()>0) {
                    array.add(sb.toString());
                    sb.append(term.charAt(i));
                    fullstr.add(sb.toString());
                    sb = new StringBuffer();
                }
                if(!containChar) skip++;
            } else {
            	sb.append(term.charAt(i));
            	containChar = true;
            } 
        }
        
        if(sb.length()>0) 
        	array.add(sb.toString());
    
        if(array.size()==0) return;

        KoreanToken tk = new KoreanToken(term, startOffset, startOffset+term.length());
        tk.setPositionIncrement(posInc);
        
        // add original text
        outQueue.add(tk);

        startOffset += skip; // when begining with punctutation.
        if(hasConcatedTerm) {
            sb = new StringBuffer();
            for(String str : array) {
                sb.append(str);
            }

            // add a token with punctuation removed.
            tk = new KoreanToken(term, startOffset, startOffset+term.length());
            tk.setPositionIncrement(0);
            
            outQueue.add(tk);
            if(sb.length()==1) return;
        }

        if(hasSplitedTerm) {
            // add tokens splited by punctuation.
            int offset = 0;
            for(int i=0; i<array.size();i++) {
                int inc = i==0 ? 0 : 1;
                outQueue.add(new Token(array.get(i),startOffset+offset, inc));
                if(hasPunctation && i<fullstr.size())
                	outQueue.add(new Token(fullstr.get(i),startOffset+offset, 0));
                offset += array.get(i).length()+1;
            }
        }

    }

    private boolean containPunctuation(String term) {
        for(int i=0;i<term.length()-1;i++) {
            if(isPunctuation(term.charAt(i))) return true;
        }
        return false;
    }

    private static boolean isPunctuation(char ch) {
        switch(Character.getType(ch)) {
            case Character.SPACE_SEPARATOR:
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
            case Character.CONTROL:
            case Character.FORMAT:
            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.CONNECTOR_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.MATH_SYMBOL:
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
            case Character.OTHER_SYMBOL:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        outQueue.clear();
    }

//    private class Token {
//        int offset;
//        
//        int endoffset;
//
//        int increment = 1;
//
//        String term;
//
//        public Token(String term, int offset) {
//        	this(term,offset,1);
//        }
//
//        public Token(String term, int offset, int inc) {
//            this(term,offset,offset+term.length(),inc);
//        }
//
//        public Token(String term, int offset, int endoffset, int inc) {
//            this.term=term;
//            this.offset=offset;
//            this.endoffset=endoffset;
//            this.increment =inc;
//        }
//        
//        public int getOffset() {
//            return offset;
//        }
//
//        @SuppressWarnings("unused")
//		public void setOffset(int offset) {
//            this.offset = offset;
//        }
//
//        public int getEndOffset() {
//            return endoffset;
//        }
//
//        @SuppressWarnings("unused")
//		public void setEndOffset(int offset) {
//            this.endoffset = offset;
//        }
//        
//        public int getIncrement() {
//            return increment;
//        }
//
//        @SuppressWarnings("unused")
//		public void setIncrement(int increment) {
//            this.increment = increment;
//        }
//
//        public String getTerm() {
//            return term;
//        }
//
//        @SuppressWarnings("unused")
//		public void setTerm(String term) {
//            this.term = term;
//        }
//    }

}
