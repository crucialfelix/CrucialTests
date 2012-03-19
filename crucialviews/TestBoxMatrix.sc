

TestBoxMatrix : UnitTest {
	
	test_propagate {
		FlowView.layout({ arg f;
			var q,k;
			
			"should post 'box' on every keystroke. parent should not post".gui(f);
			
			q = BoxMatrix(f,Rect(0,0,100,100));
			q.keyDownAction = {
				"box".postln
			};
			
		}).keyDownAction = {
			"parent".postln
		}
	}
	
	test_allow_propagate {
		FlowView.layout({ arg f;
			var q,k;
			
			"should post 'box' on every keystroke. but allow parent to get it too".gui(f);
			
			q = BoxMatrix(f,Rect(0,0,100,100));
			q.keyDownAction = {
				"box".postln;
				nil // because returned nil, should propagate
			};
			
		}).keyDownAction = {
			"parent".postln
		}		
	}
}

		