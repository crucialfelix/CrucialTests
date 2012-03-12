
TestFlowView : UnitTest {
	
	var w;
	var <>leaveWindowsOpen = false;
	
	setUp {
		w = Window.new;
		if(leaveWindowsOpen,{ w.front })
	}
	tearDown {
		if(leaveWindowsOpen.not,{
			if(w.notNil,{ w.close })
		})
	}
	test_startRow {
		var f,g,a,b,c;

		f = FlowView.new(w);
		

		a = Slider(f,Rect(0,0,100,100));
		b = Slider(f,Rect(0,0,100,100));
		f.startRow;
		c = Slider(f,Rect(0,0,100,100));

	
		this.assert( a.bounds.left < b.bounds.left,"b should not be on a new row, it should be to the right of a");
		this.assert( a.bounds.left == c.bounds.left,"a and c should be aligned on the left edge");
		
	}
	
	test_removeAll {
		var f,g,k;

		//g.front;
		f = FlowView.new(w);

		ActionButton(f,"a");

		Slider(f,Rect(0,0,100,100));

		// this is the problem: the start rows don't get removed
		f.startRow;

		ActionButton(f,"next line");

		//
		k = f.children;
		f.removeAll;
		
		this.assertEquals( f.children.size, 0 , "after removeAll there should be 0 children");
		k.do({ |oldchild|
			this.assert( oldchild.isClosed,"children views should all now be isClosed : " + oldchild )
		});
	}
	
	test_flow { 
		// this is a minor change in  behavior that would affect the swing method too
		var w;
		var comp,innerFlow;
		w = Sheet({ |f|

			f.comp({ |comp|

				innerFlow = comp.flow({ arg layout;

					//ActionButton(layout,"yo");
				},Rect(100,100,100,100))
				.background = Color.blue;
		
			},Rect(0,0,500,500))
			.background_(Color.red)
	
		});


		this.assertEquals( innerFlow.absoluteBounds.moveTo(0,0),Rect(0,0, 100, 100),
					" flow should not resizeToFit if the bounds passed to it were explicit");
		w.close
	}
	
	test_indentedRemaining {
		var b,p,f;
		f = FlowView(w);
		Slider(f,100@10);
		f.startRow;
		b = f.indentedRemaining;
		p = f.bounds;
		this.assert(p.containsRect(b),"indented remaining should be inside parent");
		this.assert(p.height > b.height,"indentedRemaining height should be smaller than parent");
		this.assertEquals(p.width, (b.width + (f.decorator.gap.x)),
			"indentedRemaining width should be equal to parent plus gap");
	}
	
	/*

w = GUI.window.new;
w.front;

w.view.horz({ |h|
	GUI.staticText.new(h,100@100).string_("horz").background = Color.red;

	GUI.staticText.new(h,100@100).string_("horz").background = Color.red;

	// should be to the right of the other two
	h.scroll({ |s|
		GUI.staticText.new(s,200@200)
			.string_("scroll123456789abcdefghijklmnopqrstuvwxyz").background = Color.blue;
	},100@100)


})

*/

}

