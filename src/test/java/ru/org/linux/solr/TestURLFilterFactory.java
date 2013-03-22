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

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.UAX29URLEmailTokenizerFactory;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.Map;

/**
 */
public class TestURLFilterFactory extends BaseTokenStreamTestCase {

  public void testURL1() throws Exception {
    Reader reader = new StringReader("http://johno.jsmf.net/knowhow/ngrams/index.php?table=en-dickens-word-2gram&paragraphs=50&length=200&no-ads=on");
    UAX29URLEmailTokenizerFactory factory = new UAX29URLEmailTokenizerFactory();
    factory.setLuceneMatchVersion(TEST_VERSION_CURRENT);
    Map<String, String> args = Collections.emptyMap();
    factory.init(args);
    URLFilterFactory filterFactory = new URLFilterFactory();
    filterFactory.setLuceneMatchVersion(TEST_VERSION_CURRENT);
    filterFactory.init(args);
    Tokenizer tokenizer = factory.create(reader);
    TokenStream stream = filterFactory.create(tokenizer);
    assertTokenStreamContents(stream,
        new String[] {
            "http://johno.jsmf.net/knowhow/ngrams/index.php?table=en-dickens-word-2gram&paragraphs=50&length=200&no-ads=on",
            "johno",
            "jsmf",
            "net",
            "knowhow",
            "ngrams",
            "index", "php",
            "table","en","dickens","word","2gram","paragraphs","50","length","200","no","ads","on"
        });
  }

  public void testURL2() throws Exception {
    Reader reader = new StringReader("http://johno.jsmf.net/knowhow/ngrams/index.php");
    UAX29URLEmailTokenizerFactory factory = new UAX29URLEmailTokenizerFactory();
    factory.setLuceneMatchVersion(TEST_VERSION_CURRENT);
    Map<String, String> args = Collections.emptyMap();
    factory.init(args);
    URLFilterFactory filterFactory = new URLFilterFactory();
    filterFactory.setLuceneMatchVersion(TEST_VERSION_CURRENT);
    filterFactory.init(args);
    Tokenizer tokenizer = factory.create(reader);
    TokenStream stream = filterFactory.create(tokenizer);
    assertTokenStreamContents(stream,
        new String[] {
            "http://johno.jsmf.net/knowhow/ngrams/index.php",
            "johno",
            "jsmf",
            "net",
            "knowhow",
            "ngrams",
            "index", "php"
        });
  }

}
