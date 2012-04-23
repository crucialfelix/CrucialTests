
/*
	requires UnitTesting
*/

TestMxLoader : UnitTest {
	
	test_loadData {
		
		var mx,data,loader;
		
		mx = Mx.new;
		
		data = mx.storeArgs[0];
		mx = Mx(data);

	}
	
	test_doubleLoad {
		var mx,data,loader;
		
		mx = Mx.new;
		
		data = mx.storeArgs[0];
		mx = Mx(data);

		data = mx.storeArgs[0];
		mx = Mx(data);
	}
	
}


TestMxUnit : UnitTest {
	
	test_handlersForPlayer {
		var p,h;
		p = MxChannelInput.new;
		h = MxUnit.handlersFor(p.class);
		// know is set
		h.name;
	}
	test_handlersForPlayerInherited {
		var p,h;
		p = InstrEventListPlayer.new;
		h = MxUnit.handlersFor(p.class);
	}
	test_handlersForInstr {
		var h,p;
		h = MxUnit.handlersFor(Instr);
		this.assert( h['name'].isKindOf(Function), "'name' should be implemented in Instr handlers");
		// unless Instr did implement it later
		this.assert( h['beatDuration'].isKindOf(Function), "'beatDuration' from protoHandler should be found in Instr handlers");

		p = Instr("_test_.Sin",{ SinOsc.ar });
		h.use {
			~make.value(p);
			this.assert( ~patch.notNil,"The correct ~make from Instr.scd should run, and ~patch should be set");
		}
	}
}

TestMx : TestAbstractPlayer {

	makePlayer {
		^Mx.new
	}
	p { ^Instr("_test_.Sin",{ SinOsc.ar }) }
	test_put {
		var x;
		x = this.makePlayer;
		x.put(0,0,this.p);
	}
	test_move {
		var x,p,moved,was;
		x = this.makePlayer;
		x.put(0,0,this.p);
		was = x.at(0,0);
		x.move(0,0,1,1);
		moved = x.at(1,1);
		this.assert( moved === was , "should be in new position now");
		this.assert(x.at(0,0).isNil,"old position should be nil");
		// check if the channel has removed it from adding
		// when it did extract
	}
	test_mx_is_set {
		var x,p,unit;
		x = Mx.new;
		x.put(0,0,this.p);
		unit = x.at(0,0);
		unit.use {
			this.assert( ~mx === x,"Unit's ~mx should be set to the mx that it has been added to");
			this.assert( ~this === unit,"Unit's ~this should be set to the unit");
		}
	}
	test_varPooling {
		var x,p,q,pu,qu;
		x = this.makePlayer;
		p = Instr("_test_.Sinp",{ SinOsc.ar });
		q = Instr("_test_.Sinq",{ SinOsc.ar });
		x.put(0,0,p);
		x.put(0,1,q);
		pu = x.at(0,0);
		qu = x.at(0,1);
		pu.varPooling = true;
		qu.varPooling = true;

		pu.use {
			~variable = "variable";
		};
		qu.use {
			this.assert( ~variable == "variable","Unit's ~variable should be set to the string 'variable' from listening upstream");
		};
		
		// turn it off
		qu.varPooling = false;
		pu.use {
			~variable = "off";
		};
		qu.use {
			this.assert( ~variable != "off","Unit's ~variable should NOT be set to the string 'off' as unit's varPooling is set to off");
		}
	}
	test_varPooling2 {
		var x,p,q,r,pu,qu,ru;
		x = this.makePlayer;
		p = Instr("_test_.Sinp",{ SinOsc.ar });
		q = Instr("_test_.Sinq",{ SinOsc.ar });
		r = Instr("_test_.Sinr",{ SinOsc.ar });
		x.put(0,0,p);
		x.put(0,1,q);
		x.put(0,2,r);
		pu = x.at(0,0);
		qu = x.at(0,1);
		ru = x.at(0,2);
		pu.varPooling = true;
		// not qu
		ru.varPooling = true;

		pu.use {
			~variable = "variable";
		};
		qu.use {
			this.assert( ~variable != "variable","qu's ~variable should be NOT set to the string 'variable' from listening upstream");
		};
		ru.use {
			this.assert( ~variable == "variable","ru's ~variable should be set to the string 'variable' from listening upstream");
		};
		
	}
	
	prTestMakeForSource { arg source;
		var x;
		x = this.makePlayer;
		x.put(0,0, source );
		^x.at(0,0)
	}
	
	test_makeDocument {
		var doc,unit,path;
		path = PathName(this.class.filenameSymbol.asString);
		doc = MxDocument(path.parentPath +/+ "fixtures/document.scd");
		unit = this.prTestMakeForSource( doc );
		unit.use {
			this.assert( ~thisDocumentWasRun == true, "~thisDocumentWasRun should be set when the document was loaded");
			this.assert( ~onLoadDidHappen == true,"this.onLoad should have been executed and set the test var ~onLoadDidHappen");
		};
	}
	test_documentDidLoad {
		var x,u;
		x = Mx(
			[ [ (1 -> [ MxChannel, [ 5 ], [ 2 ], [ 3 ], [ 0.0, false, false, 1.0, true, 12.0, 2 ] ]), (10 -> [ MxInlet, 'MxChanIn' ]), (11 -> [ MxOutlet, 'MxChanOut' ]), (5 -> [ MxUnit, [ 'MxChannelInput', [  ] ], [ 6 ], [ 7 ] ]), (0 -> [ Mx, [  ], [ [ 4, 'out', AudioSpec() ] ], [ 9 ], 1 ]), (7 -> [ MxOutlet, 'out' ]), (8 -> [ MxUnit, [ 'MxDocument', MxDocument(nil, "~yesIDid=true;") ], [  ], [  ] ]), (2 -> [ MxInlet, 'MxChanIn' ]), (9 -> [ MxChannel, [ 8 ], [ 10 ], [ 11 ], [ 0.0, false, false, nil, true, 12.0, 2 ] ]), (3 -> [ MxOutlet, 'MxChanOut' ]), (4 -> [ MxOutlet, 'out' ]), (6 -> [ MxInlet, 'in' ]) ], [ [ 11, 6, nil, true ], [ 7, 2, nil, true ] ] ], nil, false, 130.0
		);
		u = x.at(0,0);
		u.use {
			this.assert( ~yesIDid.notNil, "var ~yesIDid should be set because unit's didLoad was called" )
		}
	}
	
	test_playSimple {
		var x,s;
		x = this.makePlayer;
		//s = Instr("_test.SinMX",{SinOsc.ar});
		
		s = Instr("_test.Mx.lfsaw",{ arg freq=440,amp=1.0,iphase=0.0;
				LFSaw.ar(freq,iphase,amp)
			},[],\mono);

		x.add(s);
		this.startStopStart;
	}
}

