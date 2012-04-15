

TestNumberEditor : UnitTest {
	var w;
	classvar <>front=false;
	setUp {
		w = Window.new;
		if(front,{
			w.front;
		})
	}
	tearDown {
		if(w.notNil and: {front.not},{
			w.close
		})
	}
	prDoSizeTest { arg rect;
		var g;
		Environment.use({
			var fails;
			(Quarks.local.path +/+ Quark.find("ServerTools").path +/+ "scripts" +/+ "guiDebugger.scd").loadPath;
		
			g = NumberEditor.new.gui(w,rect);
			fails = ~childrenExceedingParents.value(g.view);
			fails.do { arg view;
				("View exceeds parent:" + view + view.parent).postln;
				[view.absoluteBounds,view.parent.absoluteBounds].postln;
			};
			this.assert( fails.size == 0, "no child should exceed parent" );
		})
	}
	test_sizing_1 {
		this.prDoSizeTest(160@17);
	}
	test_sizing_2 {
		this.prDoSizeTest(20@191);
	}
	test_sizing_3 {
		this.prDoSizeTest(40@150);
	}
}

TestKrNumberEditor : UnitTest {

	test_canAccept {
		var k,c,s;
		k = KrNumberEditor(6.0,ControlSpec(0, 11, 'linear', 1, 6, ""));

		c = ControlSpec(0, 11, 'linear', 1, 6, "");

		this.assert( c.canAccept(k), "ControlSpec should canAccept KrNumberEditor");
		
		// in this case, yes.  normally you stream NumberEditors, not KrNumberEditors
		// but an InstrSpawner can accept streams of KrNumEd
		s = StreamSpec(ControlSpec(0, 11, 'linear', 1, 6, ""));

		this.assert( s.canAccept(k), "StreamSpec should canAccept KrNumberEditor");
		// and please let us know about the cheeseburger...
	}
	
	test_spec {
		var k;
		k = KrNumberEditor(440,\freq);
		this.assert(k.spec.isKindOf(ControlSpec),"is control spec");
	}
}




