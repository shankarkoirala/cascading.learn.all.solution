package fr.xebia.cascading.learn.level4;

import cascading.flow.FlowDef;
import cascading.operation.expression.ExpressionFilter;
import cascading.pipe.CoGroup;
import cascading.pipe.Each;
import cascading.pipe.Pipe;
import cascading.pipe.assembly.Discard;
import cascading.pipe.assembly.FirstBy;
import cascading.pipe.joiner.InnerJoin;
import cascading.tap.Tap;
import cascading.tuple.Fields;

import java.io.File;

/**
 * Up to now, operations were stacked one after the other. But the dataflow can
 * be non linear, with multiples sources, multiples sinks, forks and merges.
 */
public class NonLinearDataflow {

    /**
     * Use {@link CoGroup} in order to know the party of each presidents.
     * You will need to create (and bind) one Pipe per source.
     * You might need to correct the schema in order to match the expected results.
     * <p>
     * presidentsSource field(s) : "year","president"
     * partiesSource field(s) : "year","party"
     * sink field(s) : "president","party"
     *
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch05-pipe-assemblies.html#_cogroup
     */
    public static FlowDef cogroup(Tap<?, ?, ?> presidentsSource, Tap<?, ?, ?> partiesSource,
                                  Tap<?, ?, ?> sink) {
		Pipe presidentPipe = new Pipe("president");
		Pipe partyPipe = new Pipe("party");

        Pipe pipe = new CoGroup("cogroup", presidentPipe, new Fields("year"), partyPipe, new Fields("year"), new Fields("year1", "president", "year2", "party"), new InnerJoin());

        pipe = new Discard(pipe, new Fields("year2", "year1"));
        return FlowDef.flowDef()
                .addSource(presidentPipe, presidentsSource).addSource(partyPipe, partiesSource).addTail(pipe).addSink(pipe, sink);
    }

    /**
     * Split the input in order use a different sink for each party. There is no
     * specific operator for that, use the same Pipe instance as the parent.
     * You will need to create (and bind) one named Pipe per sink.
     * <p>
     * source field(s) : "president","party"
     * gaullistSink field(s) : "president","party"
     * republicanSink field(s) : "president","party"
     * socialistSink field(s) : "president","party"
     * <p>
     * In a different context, one could use {@link PartitionTap} in order to arrive to a similar results.
     *
     * @see http://docs.cascading.org/cascading/3.0/userguide/ch15-advanced.html#partition-tap
     */
    public static FlowDef split(Tap<?, ?, ?> source,
                                Tap<?, ?, ?> gaullistSink, Tap<?, ?, ?> republicanSink, Tap<?, ?, ?> socialistSink) {

        Pipe pipe = new Pipe("split");

        ExpressionFilter expressionFilter = new ExpressionFilter("!party.equals(\"Gaullist\")", String.class);

        pipe = new Each(pipe,new Fields("president", "party"), expressionFilter );


        return FlowDef.flowDef()
                .addSource(pipe,source).addTail(pipe).addSink(pipe, gaullistSink);
    }

}
