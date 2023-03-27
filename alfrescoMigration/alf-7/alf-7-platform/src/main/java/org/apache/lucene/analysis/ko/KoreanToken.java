package org.apache.lucene.analysis.ko;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.ko.morph.AnalysisOutput;

import java.util.List;

/**
 * Created by SooMyung(soomyung.lee@gmail.com) on 2014. 7. 28.
 */

public class KoreanToken extends Token {

    private boolean endWithPunctuation = false;

    private List<AnalysisOutput> outputs = null;


    /** Constructs a Token will null text. */
    public KoreanToken() {
    }

    /** Constructs a Token with null text and start & end
     *  offsets.
     *  @param start start offset in the source text
     *  @param end end offset in the source text */
    public KoreanToken(int start, int end) {
      super(start, end);
    }

    /** Constructs a Token with null text and start & end
     *  offsets plus the Token type.
     *  @param start start offset in the source text
     *  @param end end offset in the source text
     *  @param typ the lexical type of this Token */
    public KoreanToken(int start, int end, String typ) {
    	super(start, end, typ);
    }

    /**
     * Constructs a Token with null text and start & end
     *  offsets plus flags. NOTE: flags is EXPERIMENTAL.
     *  @param start start offset in the source text
     *  @param end end offset in the source text
     *  @param flags The bits to set for this token
     */
    public KoreanToken(int start, int end, int flags) {
    	super(start, end, flags);
    }

    /** Constructs a Token with the given term text, and start
     *  & end offsets.  The type defaults to "word."
     *  <b>NOTE:</b> for better indexing speed you should
     *  instead use the char[] termBuffer methods to set the
     *  term text.
     *  @param text term text
     *  @param start start offset
     *  @param end end offset
     */
    public KoreanToken(String text, int start, int end) {
    	super(text, start, end);
    }

    /** Constructs a Token with the given text, start and end
     *  offsets, & type.  <b>NOTE:</b> for better indexing
     *  speed you should instead use the char[] termBuffer
     *  methods to set the term text.
     *  @param text term text
     *  @param start start offset
     *  @param end end offset
     *  @param typ token type
     */
    public KoreanToken(String text, int start, int end, String typ) {
     super(text, start, end, typ);
    }

    /**
     *  Constructs a Token with the given text, start and end
     *  offsets, & type.  <b>NOTE:</b> for better indexing
     *  speed you should instead use the char[] termBuffer
     *  methods to set the term text.
     * @param text
     * @param start
     * @param end
     * @param flags token type bits
     */
    public KoreanToken(String text, int start, int end, int flags) {
      super(text, start, end, flags);
    }

    public KoreanToken(String term, int offset, int posInc, List<AnalysisOutput> outputs) {
        this(term, offset, posInc);
        this.outputs = outputs;
    }

    public boolean isEndWithPunctuation() {
        return endWithPunctuation;
    }

    public void setEndWithPunctuation(boolean endWithPunctuation) {
        this.endWithPunctuation = endWithPunctuation;
    }

    public List<AnalysisOutput> getOutputs() {
        return outputs;
    }

    public void setOutputs(List<AnalysisOutput> outputs) {
        this.outputs = outputs;
    }
}
