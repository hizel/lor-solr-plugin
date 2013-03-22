/*
 * Copyright 1998-2012 Linux.org.ru
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.org.linux.solr;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizer;
import org.apache.lucene.analysis.tokenattributes.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class URLFilter extends TokenFilter {
  private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
  private final TypeAttribute typeAtt = addAttribute(TypeAttribute.class);
  private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);
  private final KeywordAttribute keywordAtt = addAttribute(KeywordAttribute.class);

  private final String URLTokenType = UAX29URLEmailTokenizer.TOKEN_TYPES[UAX29URLEmailTokenizer.URL];

  private List<String> buffer;
  private State savedState;

  protected URLFilter(TokenStream ts) {
    super(ts);
  }

  @Override
  public final boolean incrementToken() throws IOException {

    if(buffer != null && !buffer.isEmpty()) {
      String term = buffer.remove(0);
      restoreState(savedState);
      posIncAtt.setPositionIncrement(0);
      termAtt.copyBuffer(term.toCharArray(), 0, term.length());
      termAtt.setLength(term.length());
      return true;
    }

    if (!input.incrementToken()) {
      return false;
    }

    if (keywordAtt.isKeyword()) {
      return true;
    }

    if(URLTokenType.equals(typeAtt.type())) {
      buffer = tokenizeURL(termAtt.toString());
    }

    if(buffer == null || buffer.isEmpty()) {
      return true;
    }

    String term = buffer.remove(0);
    termAtt.copyBuffer(term.toCharArray(), 0, term.length());
    termAtt.setLength(term.length());

    if(!buffer.isEmpty()) {
      savedState = captureState();
    }

    return true;
  }

  @Override
  public void reset() throws IOException {
    super.reset();
    buffer = null;
  }

  private void parseTokens(String str, List<String> list) {
    if(str != null) {
      for(String v : str.split("\\W")) {
        if(v.length() != 0) {
          list.add(v);
        }
      }
    }
  }

  private List<String> tokenizeURL(String str) {
    List<String> ret = new ArrayList<String>();
    ret.add(str);
    try {
      URL url = new URL(str);
      parseTokens(url.getHost(), ret);
      parseTokens(url.getPath(), ret);
      parseTokens(url.getQuery(), ret);
      return ret;
    } catch (MalformedURLException e) {
      return ret;
    }
  }

}
