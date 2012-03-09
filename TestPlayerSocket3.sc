
// this tests a control rate PlayerSocket

TestPlayerSocket3 : TestPlayerSocket2 {
	var q,r;
	
	makePlayer {
		q = Patch({ SinOsc.kr(40.midicps) * 0.05 });
		r = Patch({ SinOsc.kr(52.midicps) * 0.05 });
		^PlayerSocket.new(\control,1);
	}
	makeBus {
		^Bus.control(s,1);
	}

	test_setSource {
		

		this.startPlayer;

		player.preparePlayer(q);
		this.wait({ q.isPrepared },"waiting for player socket to prepare patch for play");
		
		player.setSource(q);
		this.wait({ q.isPlaying },"waiting for q to play");

		player.preparePlayer(r);
		this.wait({ r.isPrepared },"waiting for player socket to prepare patch for play");
		
		player.setSource(r);
		this.wait({ r.isPlaying },"waiting for r to play");
		
		this.wait({ q.isPlaying.not },"waiting for q to stop playing");
		
		this.stopPlayer;
		
	}

	test_setSourceToBundle {

		this.startPlayer;

		player.preparePlayer(q);
		this.wait({ q.isPrepared },"waiting for player socket to prepare patch for play");
		
		player.setSourceToBundle(q,bundle,0.1);

		this.stopPlayer;

	}
	test_prepareToBundle {
		group = Group.basicNew(s);
		bus = this.makeBus;
		player.prepareToBundle(group,bundle,true,bus);
		this.assertEquals(player.group,group);
		this.assertEquals(player.bus,bus);
		this.assert(player.envdSource.notNil,"envdSource should exist");
	}


	
}

