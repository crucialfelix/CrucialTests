
TestBusDriver : TestAbstractPlayer {
	
	makePlayer {
		^BusDriver(440,\freq)
	}
	test_isPlaying {
		var p;
		p = Patch({ |freq|
			SinOsc.ar(freq)
		},[
			player
		]);
		
		p.play;
		this.wait({p.isPlaying},"waiting for patch to play");
		
		this.assert(player.isPlaying,"bus driver should now be playing");
	}

}



