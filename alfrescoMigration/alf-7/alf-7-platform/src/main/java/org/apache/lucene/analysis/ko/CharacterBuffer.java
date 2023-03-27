package org.apache.lucene.analysis.ko;

public class CharacterBuffer {
	final char[] buffer;
    int offset;
    int length;
    // NOTE: not private so outer class can access without
    // $access methods:
    char lastTrailingHighSurrogate;
    
    CharacterBuffer(char[] buffer, int offset, int length) {
      this.buffer = buffer;
      this.offset = offset;
      this.length = length;
    }
    
    /**
     * Returns the internal buffer
     * 
     * @return the buffer
     */
    public char[] getBuffer() {
      return buffer;
    }
    
    /**
     * Returns the data offset in the internal buffer.
     * 
     * @return the offset
     */
    public int getOffset() {
      return offset;
    }
    
    /**
     * Return the length of the data in the internal buffer starting at
     * {@link #getOffset()}
     * 
     * @return the length
     */
    public int getLength() {
      return length;
    }
    
    /**
     * Resets the CharacterBuffer. All internals are reset to its default
     * values.
     */
    public void reset() {
      offset = 0;
      length = 0;
      lastTrailingHighSurrogate = 0;
    }
}
