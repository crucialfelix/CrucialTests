
TestCCBank : UnitTest {

	var c;

	c {
		^(c ?? {c = CCBank.new});
	}
	tearDown { c = nil }

	test_setCtlnum {
		var ccr;
		this.c.add(\freq);
		ccr = this.c.at(\freq);
		this.c.setCtlnum(ccr,7);

		this.assert( CCResponder.ccnumr[7].includes(ccr), "ccr should be in ccnum slot for 7");
	}

}


TestMxControlRecorder : MxAppTester {

	test_remove {
		var r;
		x = Mx.new.app;
		x.add( Instr("_test.rmv",{ arg freq=440,iphase=0.0,width=0.5,amp=1.0;
				VarSaw.ar(freq,iphase,width,amp)
			}));
			
		r = x.add( MxControlRecorder.new );

		r.remove
		/*

ERROR: ID not found in registery for: out:9#_test.rmv:freq
CALL STACK:
	Exception:reportError   0x122df6ce8
		arg this = <instance of Error>
	Nil:handleError   0x122df2148
		arg this = nil
		arg error = <instance of Error>
	Thread:handleError   0x122de8b68
		arg this = <instance of Thread>
		arg error = <instance of Error>
	Object:throw   0x122daab88
		arg this = <instance of Error>
	< FunctionDef in Method Mx:unregister >   0x122d646f8
		arg in = <instance of MxOutlet>
	ArrayedCollection:do   0x122d68168
		arg this = [*4]
		arg function = <instance of Function>
		var i = 0
	Mx:unregister   0x122d656c8
		arg this = <instance of Mx>
		arg uid = 17
		var item = <instance of MxUnit>
	Mx:prPutToChannel   0x11e1c4978
		arg this = <instance of Mx>
		arg channel = <instance of MxChannel>
		arg index = 0
		arg object = nil
		var unit = nil
		var old = <instance of MxUnit>

		*/
	}
}


TestAPC40 : MxAppTester {

	test_add {
		var x;
		MxUnit.initClass;
		x = Mx.new.app;
		x.add(APC40.new);
	}
}
