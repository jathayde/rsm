package com.seanreilly.apps.rsm.controller;

import javax.sound.sampled.*;
import java.io.*;

public class Main {

  private static final short MAX_SHORT = (short)0x7fff;
  private static final short MIN_VALUE = (short)0xffff;
  private SourceDataLine line = null;

  
  private static short wave[];
  private static byte scratch[];

  //private static byte wave[];
  static {
    wave = new short[4000]; //new short[943];
    for(int i=0; i<wave.length; i++) {
      //wave[i] = (short)(Math.sin(i/(float)wave.length)*MAX_SHORT);
      wave[i] = (short)(((i%1000)/1000.0)*MAX_SHORT);
    }

    scratch = new byte[1000*2]; //new short[943];
    int tmpShort;
    for(int i=0; i<scratch.length; i+=2) {
      //tmpShort = (short)(Math.sin(Math.PI*i/(float)scratch.length)*MAX_SHORT);
      tmpShort = (short)(((i%100)/100.0)*MAX_SHORT);
      scratch[0] = (byte)((tmpShort & 0xff00) >> 8);
      scratch[i+1] = (byte)(tmpShort & 0x00ff);
    }
  }


  private MainWindow mainWindow = null;
  private Thread sourceThread = null;
  private Thread infoThread = null;
  private SoundSource source = null;

  private byte soundBuffer[] = null;
  private int bufferSize = 0;
  
  private float yValue = 0;
  private float xValue = 0;
  private int intX = 0;
  private boolean up = true;
  private short increment = 0;
  private short oldval = 0;
  private static final int MAX_INCREMENT = 50;

  private boolean useScratch = false;
  private boolean hold = false;
  private boolean smoothing = false;
  private boolean useWave = false;
  private boolean reverseWave = false;

  private static final short getShortVal(double f) {
    return (short)(f*MAX_SHORT);
  }
  
  Main(String argv[]) {
    mainWindow = new MainWindow(this);
    source = new SoundSource();
    sourceThread = new Thread(source);
    infoThread = new Thread(new Runnable() {
        public void run() {
          while(true) {
            System.out.print("up=");
            System.out.print(up);
            System.out.print("   inc=");
            System.out.print(+increment);
            //System.out.print("   xval=");
            //System.out.print(+xByte);
            System.out.print("\t\t");
            for(int i=0; i<bufferSize; i++) {
              System.out.print(soundBuffer[i]);
              System.out.print(' ');
            }
            System.out.println("");
            try { Thread.currentThread().sleep(10000); } catch (Throwable t) {}
          }
        }
      });

  }

  void go() {
    mainWindow.setVisible(true);
    sourceThread.setPriority(Thread.MAX_PRIORITY);
    sourceThread.start();
    infoThread.setPriority(Thread.MIN_PRIORITY);
    infoThread.start();
  }

  public void setYValue(float value) {
    if(useScratch) {
      float diff = Math.abs(value-yValue);
      int scratchLength = (int)(diff * scratch.length);
      scratchLength = scratchLength - (scratchLength%2);
      if(line!=null)
	line.write(scratch, 0, scratchLength);
      this.yValue = value;
    } else {
      this.yValue = value;
      this.increment = (short)(value * MAX_INCREMENT);
    }
  }

  public void setXValue(float value) {
    this.xValue = value;
    this.intX = (int)(value*10);
    //this.xByte = (byte)(value * 128);
    //this.minRange = (int)(value * bufferSize) - 2;
    //this.maxRange = (int)(value * bufferSize) + 2;
    //computeSound();
  }

  private final void computeSound() {
    if(soundBuffer==null) return;
    byte lowBits = (byte)(yValue * 128);
    byte highBits = (byte)(xValue * 128);
    int startRange = (int)(xValue*bufferSize);
    int endRange = startRange+2;
    for(int i=bufferSize-1; i>=0; i--) {
      if(i>=startRange && i<=endRange) 
        soundBuffer[i] = lowBits;
      else
        soundBuffer[i] = (byte)0;
    }
  }

  public void setUseSmoothing(boolean useSmoothing) {
    if(useSmoothing!=this.smoothing) {
      this.smoothing = useSmoothing;
      System.err.println("smoothing: "+this.smoothing);
      tick = 0;
    }
  }

  public void setUseWave(boolean useWave) {
    if(useWave!=this.useWave) {
      this.useWave = useWave;
      System.err.println("wave: "+this.useWave);
      //tick = 0;
    }
  }

  public void setUseScratch(boolean useScratch) {
    if(useScratch!=this.useScratch) {
      this.useScratch = useScratch;
      System.err.println("wave: "+this.useScratch);
      //tick = 0;
    }
  }

  public void setReverseWave(boolean reverseWave) {
    if(reverseWave!=this.reverseWave) {
      this.reverseWave = reverseWave;
      System.err.println("reverse wave: "+this.reverseWave);
      //tick = 0;
    }
  }

  public void setHold(boolean hold) {
    if(this.hold!=hold) {
      this.hold = hold;
      System.err.println("hold: "+this.hold);
      //tick = 0;
    }
  }

  private boolean top = false;
  private int tick = 0;

  private final boolean tick() {
    final int duration = 0;
    short newval;
    int inc = up ? increment : -increment;

    if(useScratch) return false;

    if(reverseWave) {
      tick+=(int)(yValue*5);
      if(tick>=wave.length) {
	tick = 0;
	//System.err.println("resetting tick");
      }
      newval = wave[tick];

      oldval = newval;
      //soundBuffer[0] = (byte)((newval & 0xff00) >> 8);
      //soundBuffer[1] = (byte)(newval & 0x00ff);
      soundBuffer[1] = (byte)((newval & 0xff00) >> 8);
      soundBuffer[0] = (byte)(newval & 0x00ff);
    } else if(useWave) {
      tick-=(int)(yValue*5);
      if(tick<0) {
	tick = wave.length-1;
	//System.err.println("resetting tick");
      }
      newval = wave[tick];

      oldval = newval;
      //soundBuffer[0] = (byte)((newval & 0xff00) >> 8);
      //soundBuffer[1] = (byte)(newval & 0x00ff);
      soundBuffer[1] = (byte)((newval & 0xff00) >> 8);
      soundBuffer[0] = (byte)(newval & 0x00ff);
    } else {
      newval = (short) (oldval + inc);

      // shift the wave in the other direction if we are close
      // enough to the min/max
      if(up && Short.MAX_VALUE-inc < newval) up = false;
      else if(!up && Short.MIN_VALUE+inc > newval) up = true;
      oldval = newval;
      newval = (short)(newval*xValue);
      //soundBuffer[0] = (byte)((newval & 0xff00) >> 8);
      //soundBuffer[1] = (byte)(newval & 0x00ff);
      soundBuffer[1] = (byte)((newval & 0xff00) >> 8);
      soundBuffer[0] = (byte)(newval & 0x00ff);
    }
    if(hold) {
      soundBuffer[0] = (byte)0;
      soundBuffer[1] = (byte)0;
    }
    return true;
  }
  
  private class SoundSource
    implements Runnable
  {
    private AudioInputStream audioInputStream;
    
    SoundSource() {
    }
    
    public void run() {
      try {
        /*
        Mixer.Info mixers[] = AudioSystem.getMixerInfo();
        Mixer mixer = null;
        for(int i=0; mixers!=null && i<mixers.length; i++) {
          System.err.println(">>>mixer: "+mixers[i]);
          mixer = AudioSystem.getMixer(mixers[i]);
        }
        */
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 11025f, 16, 1, 2, 11025f, false);
        //AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100f, 16, 1, 2, 11025f, false);
        System.err.println("sample rate: "+format.getSampleRate());
        System.err.println("sampleSizeInBits: "+format.getSampleSizeInBits());
        System.err.println("channels: "+format.getChannels());
        System.err.println("signed: true");
        System.err.println("frameSize: "+format.getFrameSize());
        System.err.println("frameRate: "+format.getFrameRate());
        System.err.println("bigEndian: "+format.isBigEndian());
        System.err.println("encoding: "+format.getEncoding());

        // define the required attributes for our line, 
        // and make sure a compatible line is supported.
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
          System.err.println("Line matching " + info + " not supported.");
          return;
        }
        
        /*
        // get and open the source data line for playback.
        Line sourceLines[] = mixer.getSourceLines();
        System.err.println("source lines: "+sourceLines.length);
        for(int i=0; sourceLines!=null && i<sourceLines.length; i++) {
          System.err.println(">>>> source line: "+sourceLines[i]);
        }
        */
        
        line = (SourceDataLine) AudioSystem.getLine(info);
        System.err.println("source data line: "+line);
        
        line.open(format, 256);
        
        // play back the captured audio data
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;

        bufferSize = frameSizeInBytes;//bufferLengthInFrames;// * frameSizeInBytes;
        
        soundBuffer = new byte[bufferSize];
        for(int i=0; i<bufferSize; i++) soundBuffer[i] = (byte)0;
        System.err.println("buffer size: "+bufferSize);

        // start the source data line
        line.start();
        
        while (true) {
          line.write(soundBuffer, 0, frameSizeInBytes);
          //line.write(soundBuffer, 0, frameSizeInBytes);
	  if(useScratch) {
	    Thread.currentThread().sleep(100);
	  } else {
	    line.write(soundBuffer, 0, frameSizeInBytes);
	    tick();
	  }
          //Thread.currentThread().sleep(10000);
        }

      } catch (Exception e) {
        System.err.println("Exception playing input: "+e);
        e.printStackTrace(System.err);
      } finally {
        // we reached the end of the stream.  let the data play out, then
        // stop and close the line.
        try {
          line.drain();
          line.stop();
          line.close();
        } catch (Throwable t) {}
      }
                  
    }
  }

  public static void main(String[] args) {
    Main m = new Main(args);
    m.go();
  }
}



