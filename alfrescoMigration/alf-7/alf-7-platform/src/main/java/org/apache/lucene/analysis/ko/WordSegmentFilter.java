package org.apache.lucene.analysis.ko;

import java.io.IOException;

import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ko.morph.AnalysisOutput;
import org.apache.lucene.analysis.ko.morph.CompoundEntry;
import org.apache.lucene.analysis.ko.morph.MorphException;
import org.apache.lucene.analysis.ko.morph.PatternConstants;
import org.apache.lucene.analysis.ko.morph.WordSegmentAnalyzer;
import org.apache.lucene.analysis.ko.utils.DictionaryUtil;
import org.apache.lucene.analysis.ko.utils.MorphUtil;

public final class WordSegmentFilter extends TokenFilter {

    private final LinkedList<KoreanToken> outQueue = new LinkedList<KoreanToken>();
    
    private boolean modeQueue = false;
    
    private WordSegmentAnalyzer segmentAnalyzer;
    
    private boolean hasOrigin = true;
    
    // used to check whether if incomming token is produced by the same text as the previous token at korean filter
    private List<AnalysisOutput> morphOutputs;
    
	protected WordSegmentFilter(TokenStream input) {
		super(input);
		segmentAnalyzer = new WordSegmentAnalyzer();
	}

	protected WordSegmentFilter(TokenStream input, boolean hasOrigin) {
		this(input);
		this.hasOrigin = hasOrigin;
	}
	
	public final Token next(final Token reusableToken) throws IOException {
	    assert reusableToken != null;
	    
	    if (!outQueue.isEmpty()) {
	    	outQueue.removeFirst();
	    }
	    
	    modeQueue = false;
	    
	    while(outQueue.isEmpty()) {
        	Token token = input.next(reusableToken);
        	
        	if(token==null || !(token instanceof KoreanToken)) return token;

        	KoreanToken kToken = (KoreanToken)token;
        	
        	if(morphOutputs!=null) {
        		if(morphOutputs==kToken.getOutputs()) {
        			continue; // current incomming token is removed because of it is duplicated to Word Segment output.
        		}else {
        			morphOutputs = null;
        		}
        	}
        	
        	assert kToken.getOutputs().size()>0;
        	
        	if(kToken.getPositionIncrement()==0 || 
        			kToken==null || kToken.getOutputs().size()==0 
        			|| kToken.getOutputs().get(0).getScore()>AnalysisOutput.SCORE_COMPOUNDS ||
        					(kToken.getOutputs().get(0).getScore()==AnalysisOutput.SCORE_COMPOUNDS && 
        					!(containJosa(kToken) || MorphUtil.hasVerbOnly(kToken.getOutputs().get(0).getStem()))))
        		return kToken;
        	
        	String term = kToken.term();
        	int startOffset = kToken.startOffset();
        	int posInc = kToken.getPositionIncrement();
        	
        	try {
        		
				if(hasOrigin) {
					KoreanToken tk = new KoreanToken(term, startOffset, startOffset+term.length());
					tk.setPositionIncrement(posInc);
					
					outQueue.add(tk);
				}
				
				List<List<AnalysisOutput>> segments = segmentAnalyzer.analyze(term);
				if(segments.size()<2) {
					return outQueue.removeFirst();
				}
				
				int offset = 0;
				for(int i=0;i<segments.size();i++)
				{
					assert segments.get(i).size()>0;
					
					String word = segments.get(i).get(0).getSource();
					List<CompoundEntry> entries = segments.get(i).get(0).getCNounList();
					posInc = i==0 ? 0 : 1;
					
					if(hasOrigin) {
						KoreanToken kt = new KoreanToken(word,startOffset, startOffset+word.length());
						kt.setPositionIncrement(posInc);
						outQueue.add(kt);
					}
					
					if(entries.size()>1) {
						if(!hasOrigin || !word.equals(segments.get(i).get(0).getStem())) {
							KoreanToken kt = new KoreanToken(segments.get(i).get(0).getStem(),startOffset+offset, startOffset+offset+segments.get(i).get(0).getStem().length());
							kt.setPositionIncrement(hasOrigin ? 0 : posInc);
							outQueue.add(kt);
						}
							
						
						int innerOffset = offset;
						for(int k=0;k<entries.size();k++) {
							CompoundEntry ce = entries.get(k);
							int innerPosInc = k==0 ? 0 : 1;
							
							KoreanToken kt = new KoreanToken(ce.getWord(),startOffset+innerOffset, startOffset+innerOffset+ce.getWord().length());
							kt.setPositionIncrement(innerPosInc);
							outQueue.add(kt);
							
							innerOffset += ce.getWord().length();
						}
					} else {
						if(segments.get(i).get(0).getPatn()>=PatternConstants.PTN_VM && segments.get(i).get(0).getPatn()<PatternConstants.PTN_ZZZ) {
							if(!hasOrigin) {
								KoreanToken kt = new KoreanToken(word,startOffset+offset, startOffset+offset+word.length());
								kt.setPositionIncrement(posInc);
								outQueue.add(kt);
							}
						} else {
							if(!hasOrigin || !word.equals(segments.get(i).get(0).getStem())) {
								KoreanToken kt = new KoreanToken(segments.get(i).get(0).getStem(),startOffset+offset, startOffset+offset+segments.get(i).get(0).getStem().length());
								kt.setPositionIncrement(hasOrigin ? 0 : posInc);
								outQueue.add(kt);
							}	
						}
					}
					
					offset += word.length();
				}
				
				modeQueue = true;
				morphOutputs = kToken.getOutputs();
				
	            if (!outQueue.isEmpty()) {
					return outQueue.removeFirst();
	            }
	            
			} catch (MorphException e) {
				throw new RuntimeException(e);
			}	
	    }
	    
	    return null;
	}
	  
	private boolean containJosa(KoreanToken kToken)  {
		
		List<AnalysisOutput> outputs = kToken.getOutputs();
		if(outputs.size()==0 || outputs.get(0).getCNounList().size()==0) return false;
		
		try {
			List<CompoundEntry> entries = outputs.get(0).getCNounList();
			for(int i=0;i<entries.size();i++) {
				if(DictionaryUtil.existJosa(entries.get(i).getWord())) return true;
			}
		}catch(MorphException e) {
			throw new RuntimeException(e);
		}

		return false;
	}
	
    
    @Override
    public void reset() throws IOException {
        super.reset();
        outQueue.clear();
    }
    
}
