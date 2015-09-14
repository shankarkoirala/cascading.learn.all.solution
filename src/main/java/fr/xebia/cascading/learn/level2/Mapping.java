package fr.xebia.cascading.learn.level2;

import cascading.flow.FlowDef;
import cascading.operation.Filter;
import cascading.operation.Function;
import cascading.operation.expression.ExpressionFilter;
import cascading.operation.expression.ExpressionFunction;
import cascading.operation.regex.RegexSplitGenerator;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Rename;
import cascading.tap.Tap;
import cascading.tuple.Fields;

/**
 * Now that we know how to maintain a clean schema, it is time to actually manipulate each input individually.
 * {@link Each} will allow you to execute a function for all inputs, a bit like a for loop.
 */
public class Mapping {

    /**
     * Use a {@link Each} in order to {@link Filter} out "line"s which do not contains "Hadoop".
     * {@link ExpressionFilter} makes it very easy for simple case.
     * <p>
     * source field(s) : "line"
     * sink field(s) : "line"
     *
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch05-pipe-assemblies.html#each-every
     */
    public static FlowDef filterWithExpression(Tap<?, ?, ?> source, Tap<?, ?, ?> sink) {
		Pipe pipe = new Pipe("filter");

        ExpressionFilter expressionFilter = new ExpressionFilter("!line.toLowerCase().contains(\"hadoop\")", String.class);
        pipe = new Each(pipe, new Fields("line"), expressionFilter);

        return FlowDef.flowDef().addSource(pipe, source).addTail(pipe).addSink(pipe,sink);
    }

    /**
     * Use a {@link Each} in order to transform every "line" in lowercase.
     * {@link Function} allows to change the input, and again
     * {@link ExpressionFunction} is really helpful for simple case.
     * <p>
     * source field(s) : "line"
     * sink field(s) : "line"
     *
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch05-pipe-assemblies.html#each-every
     */
    public static FlowDef transformWithExpression(Tap<?, ?, ?> source, Tap<?, ?, ?> sink) {

        Pipe pipe = new Pipe("filter");

        ExpressionFunction expressionFunction= new ExpressionFunction(new Fields("line"), "line.toLowerCase()", String.class);
        pipe = new Each(pipe, new Fields("line"), expressionFunction);

        return FlowDef.flowDef().addSource(pipe, source).addTail(pipe).addSink(pipe,sink);
    }

    /**
     * Split each "line" into "word"s using a custom {@link Function}.
     * The provided function do only the identity.
     * <p>
     * input field(s) : "line"
     * output field(s) : "word"
     *
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch05-pipe-assemblies.html#each-every
     * <p>
     * Of course, for a real use case, regular expressions could be used with existent functions
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch16-operations.html#_regular_expression_operations
     */
    public static FlowDef transformWithACustomFunction(Tap<?, ?, ?> source,
                                                       Tap<?, ?, ?> sink) {

        Pipe pipe = new Pipe("funcation");

        pipe = new Each(pipe, new Fields("line"), new RegexSplitGenerator(new Fields("word"), "\\s+"));


       /* Pipe pipe = new Each("split", new Fields("line"), new CustomSplitFunction<Object>(new Fields("word")), Fields.SWAP);

        pipe = new Rename(pipe, new Fields("word"), new Fields("word"));*/


        return FlowDef.flowDef()//
                .addSource(pipe, source) //
                .addTail(pipe)//
                .addSink(pipe, sink);
    }


}
