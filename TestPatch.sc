
TestPatch : TestAbstractPlayer {

	var p,i;

	makePlayer {
		// patch
		i = Instr("help-Patch",{ arg freq=100,amp=1.0;
				SinOsc.ar([freq,freq + 30],0,amp)
			});
		p = Patch(i,[ 500,	0.3 ]);
		^p
	}
	makeBus {
		^Bus.audio(s,2)
	}
	
	//	AbstractPlayer.bundleClass = MixedBundleTester;
	//	MixedBundleTester.reset;
	//	InstrSynthDef.clearCache(Server.default);
	//}

	test_play {
		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");
		0.3.wait;
		p.stop;
		this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		p.free;
		this.wait( {p.isPrepared.not},"after free, patch should not be ready for play");

		//0.2.wait;// grr, have to wait for the bundles to be really sent, and they still aren't ?
		//this.assertEquals(MixedBundleTester.bundlesSent.size,2,"should be only two bundles sent: prepare/play and stop");
		//MixedBundleTester.bundlesSent.insp;
	}
	
	test_prepare {
		p.prepareForPlay;
		
		this.wait( {p.isPrepared},"wait for patch to be ready");
		
		p.play;
		this.wait( {p.isPlaying},"wait for patch to play");

		p.free;
		this.wait({ p.isPrepared.not},"wait for patch to be un-ready after free");

		//p.stop;
		//this.wait( {p.isPlaying.not},"waiting for patch to stop playing");
		
		// no longer true
		//this.assert( p.readyForPlay,"patch should still be ready for play");
		
		//p.free;
		//this.wait( {p.readyForPlay.not},"after free, patch should not be ready for play");
	}
	test_gui {
		var s;
		Instr.clearAll;
		Instr("sin",{SinOsc.ar});
		{
			s = Sheet({ arg f;
				Patch("sin").gui(f);
				Patch("sin").gui(f);
			});
			s.close;
		}.defer;
		this.wait( { s.isClosed },"waiting for window to close");
		this.assertEquals( Instr.leaves.size,1,"should only be one instr in the lib");
	}
	test_argsSetter {
		var p,k,l;
		p = Patch({ arg freq; SinOsc.ar(freq) });
		k = KrNumberEditor(440.0,\freq);
		p.args = [k];
		this.assert( p.args[0] === k, "arg should be set with KrNumberEditor");
		this.assert( p.argsForSynth[0] === k, "arg should be set with KrNumberEditor");
	}
	test_krNumberEditor {
		var k,spo;
		p = Patch(i,[ 
				k = KrNumberEditor(440,\freq)
			,	0.3 ]);
		p.play;
		0.5.wait;
		this.wait({p.isPlaying},"wait patch playing");
		0.5.wait;
		spo = p.patchIns.first.connectedTo;
		this.assert(spo.notNil,"connected to UpdatingScalarPatchOut");
		this.assert(spo.source === k,"connected to KrNumberEditor");
	}
	test_irNumberEditor {
		var ine,spo;
		p = Patch(i,[

			ine = IrNumberEditor(440,\freq),
			1.0

		]);
		p.play;
		this.wait({p.isPlaying},"wait patch playing");
		spo = p.patchIns.first;
		this.assert(spo.notNil,"control patchIn for IrNumberEditor");
		this.assertEquals( p.synthDef.controls.size,2,"synth def controls");
	}
	test_startStopStart {
		this.startStopStart;
	}
	test_subpatch {
		var p;
		p = Patch({ arg input;
				RLPF.ar(input,400)
			},[
				Patch({
					Saw.ar.dup
				})
			]);
		
		player = p;
		
		this.startStopStart;
		
		
	}
	test_playerPool {
		var players,pp;
		players = Array.fill(3,{ Patch({ SinOsc.ar(rrand(100,200).dup) }) });
		pp = PlayerPool.new(players,rate:\audio,numChannels:2);
		
		player = Patch({ arg audio;
					RLPF.ar(audio,400)
				},[
					pp
				]);
		
		this.startStopStart;
	}


	test_onPlay {
		var p,played=false;
		p = Patch({
			SinOsc.ar * EnvGen.kr(Env.perc,doneAction:2)
		});
		p.onPlay({ played = true });
		p.play;
		this.wait({
			played == true
		},"should fire onPlay");
		p.free
	}
	test_onStop {
		var p,stopped=false;
		p = Patch({
			SinOsc.ar * EnvGen.kr(Env.perc,doneAction:2)
		});
		p.onStop({ stopped = true });
		p.play;
		this.wait({
			stopped == true
		},"should fire onStop");
		p.free
	}
	test_onStop_external {
		var p,stopped=false;
		p = Patch({
			SinOsc.ar
		});
		p.onStop({ stopped = true });
		p.onPlay({
			p.stop;
		});
		p.play;
		this.wait({
			stopped == true
		},"should fire onStop");
		p.free
	}
	/*
		issue:  onStop with immediate onPlay
			results in synth set to nil by didStop
	*/
	test_onReady {
		var p,ready=false;
		p = Patch({
			SinOsc.ar * EnvGen.kr(Env.perc,doneAction:2)
		});
		p.onReady({ ready = true });
		p.prepareForPlay();
		this.wait({
			ready == true
		},"should fire onReady");
		p.free
	}
	test_freeOnStop {
		var p,stopped=false;
		p = Patch({
			SinOsc.ar * EnvGen.kr(Env.perc,doneAction:2)
		});
		p.freeOnStop;
		p.onPlay({
			p.stop
		});
		p.play;
		this.wait({
			p.status == \isFreed
		},"status should go to isFreed");
		this.assertEquals(p.status,\isFreed)
	}	

	test_onPoll {
		var p,vals;
		vals = List.new;
		p = Patch({
			var b;
			b = BrownNoise.ar(1.0);
			b.onPoll({ arg v;
				vals.add(v);
			});
			b * 0.2
		});
		p.play;
		0.5.wait;
		p.stop;
		this.assert(vals.size > 0,"should have collected some values");
		this.assert(vals.every({ arg v; v.inclusivelyBetween(-1,1) }),"values should be bipolar");
	}
	
		//p.onStop({ "stopped".postln });
		//p.play


		
	/*
	test_patchInPatch {
		
		Patch({
			Patch({ |audio|
				RLPF.ar(audio,400,0.2)
			},[
				Patch({ Saw.ar })
			]).ar;
	
		}).play	

		Patch({

			Patch({ Saw.ar }).ar
	
		}).play	

	}
	*/
}

