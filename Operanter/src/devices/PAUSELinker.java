package devices;

import java.util.Random;

import op.Defaults;
import db.DatabaseConnection;

public class PAUSELinker extends Linker{

    
    static int FIXED_PAUSE=0;
    static int UNIFORM_DIST_PAUSE=1;
    
    
    public int pauseType=0;
    public int pauseLength=1000;
    public int[] pauseBoundaries={0,1000};

    public boolean active=true;
    
    Random random;
    
    public PAUSELinker(String name, DigitalIO[] outputs, int[] triggerCodes, Defaults defaults){
        super(name, outputs, triggerCodes, defaults);
        //this.output=output;
        this.random=defaults.getRandom();
    }
    
    public void setPause(int x){
        pauseType=FIXED_PAUSE;
        pauseLength=x;    
    }
    
//    public void setUniformDistributionPause(int minimumPause, int maximumPause){
//        pauseType=UNIFORM_DIST_PAUSE;
//        pauseBoundaries[0]=minimumPause;
//        pauseBoundaries[1]=maximumPause-minimumPause;    
//    }
    
    
    public int getPause(){
        
        if (pauseType==FIXED_PAUSE){
            return pauseLength;
        }
        else if (pauseType==UNIFORM_DIST_PAUSE){
            int x=random.nextInt(pauseBoundaries[1]-pauseBoundaries[0]); //MM added -pauseBoundaries[0]. Does setUniformDistributionPause() even get used???
            return x+pauseBoundaries[0];
        }
        return 0;
    }
    
    public void turnOn(){
        active=true;
    }
    
    public void turnOff(){
        active=false;
    }
    
    public void trigger(int a){
        if (a==0){
            turnOn();
            int t=getPause();
            MakePause mp=new MakePause(t);
            mp.start();
        }
        else if (a==1){
            turnOff();
        }
    }
    
    class MakePause extends Thread{
        
        int t;
        
        public MakePause(int t){
            this.t=t;
        }

        public void run(){
            try{
                System.out.println("PAUSING FOR "+t+"ms");
            	System.out.println("Outputs Length is: " + outputs.length); //giving me 2
                Thread.sleep(t);
                if (active){
                    for (int i=0; i<outputs.length; i++){
                    	System.out.println("outputs[i] is: " + outputs[i]); //giving me "null"
                        outputs[i].trigger(triggerCodes[i]); //NullPointerException
                    }
                    System.out.println("left for loop");
                }
                System.out.println("left if statement");
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void getProperties(String key){
        pauseType=defaults.getIntProperty(key+name+"ptype");
        pauseLength=defaults.getIntProperty(key+name+"plength");
        pauseBoundaries[0]=defaults.getIntProperty(key+name+"pbound0");
        pauseBoundaries[1]=defaults.getIntProperty(key+name+"pbound1");
    }
    
    public void setProperties(String key){
        defaults.setIntProperty(key+name+"ptype", pauseType);
        defaults.setIntProperty(key+name+"plength", pauseLength);
        defaults.setIntProperty(key+name+"pbound0", pauseBoundaries[0]);
        defaults.setIntProperty(key+name+"pbound1", pauseBoundaries[1]);
    }

}

/*package devices;

import java.util.Random;

import op.Defaults;
import db.DatabaseConnection;

public class PAUSELinker extends Linker{

	
	static int FIXED_PAUSE=0;
	static int UNIFORM_DIST_PAUSE=1;
	
	
	public int pauseType=0;
	public int pauseLength=1000;
	public int[] pauseBoundaries={0,1000};

	public boolean active=true;
	
	Random random;
	
	public PAUSELinker(String name, DigitalIO[] outputs, int[] triggerCodes, Defaults defaults){
		super(name, outputs, triggerCodes, defaults);
		//this.output=output;
		this.random=defaults.getRandom();
	}
	
	public void setPause(int x){
		pauseType=FIXED_PAUSE;
		pauseLength=x;	
	}
	
	public void setUniformDistributionPause(int minimumPause, int maximumPause){
		pauseType=UNIFORM_DIST_PAUSE;
		pauseBoundaries[0]=minimumPause;
		pauseBoundaries[1]=maximumPause-minimumPause;	
	}
	
	
	public int getPause(){
		
		if (pauseType==FIXED_PAUSE){
			return pauseLength;
		}
		else if (pauseType==UNIFORM_DIST_PAUSE){
			int x=random.nextInt(pauseBoundaries[1]);
			return x+pauseBoundaries[0];
		}
		return 0;
	}
	
	public void turnOn(){
		active=true;
	}
	
	public void turnOff(){
		active=false;
	}
	
	public void trigger(int a){
		int t=getPause();
		MakePause mp=new MakePause(t);
		mp.start();
	}
	
	class MakePause extends Thread{
		
		int t;
		
		public MakePause(int t){
			this.t=t;
		}

		public void run(){
			try{
				System.out.println("PAUSING FOR "+t+"ms");
				Thread.sleep(t);
				if (active){
					for (int i=0; i<outputs.length; i++){
						outputs[i].trigger(triggerCodes[i]);
					}
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void getProperties(String key){
		pauseType=defaults.getIntProperty(key+name+"ptype");
		pauseLength=defaults.getIntProperty(key+name+"plength");
		pauseBoundaries[0]=defaults.getIntProperty(key+name+"pbound0");
		pauseBoundaries[1]=defaults.getIntProperty(key+name+"pbound1");
	}
	
	public void setProperties(String key){
		defaults.setIntProperty(key+name+"ptype", pauseType);
		defaults.setIntProperty(key+name+"plength", pauseLength);
		defaults.setIntProperty(key+name+"pbound0", pauseBoundaries[0]);
		defaults.setIntProperty(key+name+"pbound1", pauseBoundaries[1]);
	}

}
*/