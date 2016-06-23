package dataset_processors;


import dataset_models.ITIS;

public class ITISLinker extends Linker<ITIS> {

	@Override
	public ITIS get(int id) {
		
		ITIS entity = new ITIS();
		
		// TODO
		
		return entity;		
	}
	
	@Override
	public void write(ITIS itisItem, String storePath) {
		// TODO
	}


}
