

TestSimpleButton : UnitTest {
	
	var w;
	
	setUp {
		w = Window.new;
	}
	tearDown {
		if(w.notNil,{ w.close })
	}
	test_width {
		var b;
		b = SimpleButton(w,"x",{},300);
		this.assert(b.bounds.width >= 300);
	}
}


