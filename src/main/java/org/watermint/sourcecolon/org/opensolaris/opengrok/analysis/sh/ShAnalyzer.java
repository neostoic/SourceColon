/*
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */

/*
 * Copyright (c) 2005, 2010, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2013 Takayuki Okazaki.
 */
package org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.sh;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.Definitions;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.FileAnalyzerFactory;
import org.watermint.sourcecolon.org.opensolaris.opengrok.analysis.plain.PlainAnalyzerBase;
import org.watermint.sourcecolon.org.opensolaris.opengrok.configuration.Project;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;

/**
 * Analyzes Shell scripts/Conf files etc.,
 * Created on September 21, 2005
 *
 * @author Chandan
 */
public final class ShAnalyzer extends PlainAnalyzerBase {
    /**
     * Creates a new instance of ShAnalyzer
     */
    private ShSymbolTokenizer shref;
    private ShXref xref;
    private Reader dummy = new StringReader("");

    protected ShAnalyzer(FileAnalyzerFactory factory) {
        super(factory);
        shref = new ShSymbolTokenizer(dummy);
        xref = new ShXref(dummy);
    }

    @Override
    public void analyze(Document doc, Reader in) throws IOException {
        super.analyze(doc, in);
        doc.add(new Field("refs", dummy));
    }

    public TokenStream tokenStream(String fieldName, Reader reader) {
        if ("refs".equals(fieldName)) {
            shref.reInit(super.content, super.len);
            return shref;
        }
        return super.tokenStream(fieldName, reader);
    }

    /**
     * Write a cross referenced HTML file.
     *
     * @param out Writer to write HTML cross-reference
     */
    public void writeXref(Writer out) throws IOException {
        xref.reInit(content, len);
        xref.project = project;
        xref.setDefs(super.defs);
        xref.write(out);
    }

    /**
     * Write a cross referenced HTML file reads the source from in
     *
     * @param in         Input source
     * @param out        Output xref writer
     * @param defs       definitions for the file (could be null)
     */
    static void writeXref(Reader in, Writer out, Definitions defs, Project project) throws IOException {
        ShXref xref = new ShXref(in);
        xref.project = project;
        xref.setDefs(defs);
        xref.write(out);
    }
}
