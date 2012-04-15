
TestKeyResponder : UnitTest {
	
	test_propagate {
		
		FlowView.layout({ arg f;
			var q,k;
			
			"The slider should respond to letter k, all other keys should post 'parent'".gui(f);
			
			k = KeyResponder.new;
			k.register($k,function: {
				"k".postln
			});
			q = View(f,Rect(0,0,100,100));
			q.background = Color.rand;
			q.keyDownAction = k;
			
			// something to focus on
			Slider(q,Rect(0,0,100,20)).focus
			
		}).keyDownAction = {
			"parent".postln
		}
	}
	test_char {
		var w,x,k;
		FlowView.layout({ arg w;
			"control-c should print copy".gui(w);
			x = Slider.new(w);
			k = KeyResponder.new;
			k.register(   8  ,   false, false, false, true, {
				"copy".postln;
			});
			x.keyDownAction = k;
		});
	}
}

