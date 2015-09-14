package fr.xebia.cascading.learn.level5;

import cascading.flow.FlowDef;
import cascading.operation.Debug;
import cascading.operation.aggregator.Count;
import cascading.operation.aggregator.First;
import cascading.operation.expression.ExpressionFilter;
import cascading.operation.expression.ExpressionFunction;
import cascading.operation.regex.RegexReplace;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.pipe.*;
import cascading.pipe.assembly.CountBy;
import cascading.tap.Tap;
import cascading.tuple.Fields;

/**
 * You now know all the basics operators. Here you will have to compose them by yourself.
 */
public class FreestyleJobs {

    /**
     * Word count is the Hadoop "Hello world" so it should be the first step.
     * <p>
     * source field(s) : "line"
     * sink field(s) : "word","count"
     */
    public static FlowDef countWordOccurences(Tap<?, ?, ?> source, Tap<?, ?, ?> sink) {

        Pipe pipe = new Pipe("wordCount");

        RegexSplitGenerator regexFunction = new RegexSplitGenerator(new Fields("word"), "\\s+");

        ExpressionFunction expressionFunction = new ExpressionFunction(new Fields("line"), "line.toLowerCase()", String.class);

        RegexReplace replace = new RegexReplace(new Fields("word"),"([^A-Za-za-z-A-Z]+)", "");

        pipe = new Each(pipe, new Fields("line"), expressionFunction);

        pipe = new Each(pipe, new Fields("line"), regexFunction, new Fields("word") );


        pipe= new Each(pipe, new Fields("word"),replace);
//        pipe = new GroupBy(pipe, new Fields("word"), Fields.ALL);
//        pipe = new Every(pipe, new Fields("word"), new Count(new Fields("count")),Fields.ALL);

        pipe = new CountBy(pipe, new Fields("word"), new Fields("count"));

        pipe = new Each(pipe, new Debug("dddd"));





        return FlowDef.flowDef()
                .addSource(pipe, source)
                .addTail(pipe)
                .addSink(pipe, sink);
    }

    /**
     * Now, let's try a non trivial job : td-idf. Assume that each line is a
     * document.
     * <p>
     * source field(s) : "line"
     * sink field(s) : "docId","tfidf","word"
     * <p>
     * <pre>
     * t being a term
     * t' being any other term
     * d being a document
     * D being the set of documents
     * Dt being the set of documents containing the term t
     *
     * tf-idf(t,d,D) = tf(t,d) * idf(t, D)
     *
     * where
     *
     * tf(t,d) = f(t,d) / max (f(t',d))
     * ie the frequency of the term divided by the highest term frequency for the same document
     *
     * idf(t, D) = log( size(D) / size(Dt) )
     * ie the logarithm of the number of documents divided by the number of documents containing the term t
     * </pre>
     * <p>
     * Wikipedia provides the full explanation
     *
     * @see http://en.wikipedia.org/wiki/tf-idf
     * <p>
     * If you are having issue applying functions, you might need to learn about field algebra
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch04-tuple-fields.html#field-algebra
     * <p>
     * {@link First} could be useful for isolating the maximum.
     * <p>
     * {@link HashJoin} can allow to do cross join.
     * <p>
     * PS : Do no think about efficiency, at least, not for a first try.
     * PPS : You can remove results where tfidf < 0.1
     */
    public static FlowDef computeTfIdf(Tap<?, ?, ?> source, Tap<?, ?, ?> sink) {
        return null;
    }

}
