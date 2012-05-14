
TestDrivers : MxAppTester {
	
	test_ccbank_connected {
		var c,cu,before,after,u;
		c = CCBank( [(\freq -> 1 )] );
		
		cu = x.add( c );
		u = this.v;
		cu >> u.i.freq;
		u >> x.channel(u.point.x).fader;
		x.gui;

		// for the moment controllers only work while playing
		x.play;
		this.wait({ x.isPlaying },"waiting for x to play");
		u.use {
			before = ~patch.freq.value.debug("before");
			MIDIIn.doControlAction(123,9,1,64);
			after = ~patch.freq.value.debug("after");
		};
		this.assert( before != after , "value of freq input of patch should change");

		x.stop;
		this.wait({ x.isPlaying.not },"waiting for x to stop playing");
		x.free;
	}
}