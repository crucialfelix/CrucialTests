

TestPlayerRecorder : UnitTest {
	
	var recorder,tempo,s,patch;

	setUp {
		s = Server.default;
		tempo = Tempo.tempo;
		^super.setUp
	}
	tearDown {
		Tempo.tempo = tempo;
		^super.tearDown
	}
	makePlayer {
		patch = Patch({SinOsc.ar([440,441],mul:0.1)});
		recorder = PlayerRecorder(patch);
		^patch
	}
	assertNothingAllocated {
		if(s.numSynths > 0,{
			s.queryAllNodes
		});		
		this.assertEquals( s.audioBusAllocator.blocks.size,0,"there should be no busses allocated now");
		this.assertEquals( s.controlBusAllocator.blocks.size,0,"there should be no control busses allocated now");
		this.assertEquals( s.numSynths, 0, "no synths should be running");		
	}
	test_makePath {
		this.makePlayer;
		this.assert( recorder.makePath.isString );
	}
	test_synthDefToBundle {
		var defName;
		var bundle = MixedBundleTester.new;
		this.makePlayer;
		defName = recorder.synthDefToBundle(bundle);
		this.assert(bundle.includesDefName(defName));
	}
	test_free {
		this.makePlayer;
		recorder.free; // idimpotent
	}


	test_record {
		var ended = false,path;
		path = "/tmp/TestPlayerRecorder.AIFF";
		this.makePlayer;

		recorder.record(path:path,endBeat:4,
			onComplete:{ ended = true});
		this.wait({ ended },"recorder failed to end",24);

		this.assert( File.exists(path) );
		1.0.wait;
		this.assertNothingAllocated;
	}
	test_liveRecord {
		var ended = false,path;
		path = "/tmp/TestPlayerRecorder.AIFF";
		this.makePlayer;
		patch.play;
		this.wait({ patch.isPlaying},"waiting for player to play");

		recorder.liveRecord(path,{ "ended".debug; ended = true});

		AppClock.sched(4,{
			"recorder stop".debug;
			recorder.stop;
		});

		this.wait({ ended },"recorder failed to end",24);

		this.assert( patch.isPlaying,"patch should still be playing, not stopped by recording stopping");

		this.assert( File.exists(path), "file exists" + path );

		patch.free;
		this.wait({ patch.isPlaying.not },
			"patch failed to stop");
		1.0.wait;
		this.assertNothingAllocated;
	}
}

