
TestActionButton : UnitTest {
	
	test_swallows {
		// requires human interaction
		FlowView.layout({ arg f;

			"If SCViewHolder.consumeKeyDowns is true then events should not propogate".gui(f);
	
			("SCViewHolder.consumeKeyDowns:" + SCViewHolder.consumeKeyDowns).gui(f);
	
			ActionButton(f.startRow,"try pressing keys", {
				"func".postln
			})
		}).keyDownAction = {
			"parent".postln
		}
	}
}
	