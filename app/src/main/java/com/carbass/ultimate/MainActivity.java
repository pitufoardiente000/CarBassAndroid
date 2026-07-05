package com.carbass.ultimate;

import android.os.Bundle;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.media.audiofx.Virtualizer;
import com.getcapacitor.BridgeActivity;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

public class MainActivity extends BridgeActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        registerPlugin(AudioMasterPlugin.class);
        super.onCreate(savedInstanceState);
    }
}

@CapacitorPlugin(name = "AudioMaster")
class AudioMasterPlugin extends Plugin {
    private BassBoost bassBoost = null;
    private Equalizer equalizer = null;
    private Virtualizer virtualizer = null;

    private void initAudio() {
        if (bassBoost == null) {
            try {
                // Audio Session 0 le aplica los efectos GLOBALMENTE al sistema (Spotify/YouTube)
                bassBoost = new BassBoost(0, 0);
                equalizer = new Equalizer(0, 0);
                virtualizer = new Virtualizer(0, 0);

                bassBoost.setEnabled(true);
                equalizer.setEnabled(true);
                virtualizer.setEnabled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PluginMethod
    public void setBassBoost(PluginCall call) {
        initAudio();
        Integer fuerza = call.getInt("fuerza");
        if (bassBoost != null && bassBoost.getStrengthSupported()) {
            bassBoost.setStrength(fuerza.shortValue());
            call.resolve();
        } else {
            call.reject("BassBoost no soportado");
        }
    }

    @PluginMethod
    public void setTweeter(PluginCall call) {
        initAudio();
        Integer ganancia = call.getInt("ganancia");
        if (equalizer != null) {
            short ultimaBanda = (short) (equalizer.getNumberOfBands() - 1);
            equalizer.setBandLevel(ultimaBanda, ganancia.shortValue());
            call.resolve();
        } else {
            call.reject("Ecualizador no disponible");
        }
    }

    @PluginMethod
    public void setVirtualizer(PluginCall call) {
        initAudio();
        Boolean estado = call.getBoolean("estado", false);
        if (virtualizer != null) {
            virtualizer.setStrength(estado ? (short) 1000 : (short) 0);
            call.resolve();
        } else {
            call.reject("Efecto 6D no soportado");
        }
    }
    
    @PluginMethod
    public void setDolby(PluginCall call) {
        initAudio();
        Integer nivel = call.getInt("nivel");
        if (equalizer != null) {
            short bandaMediaAlta = (short) (equalizer.getNumberOfBands() - 2);
            short nivelFinal = (short) ((nivel / 15.0) * 1000);
            equalizer.setBandLevel(bandaMediaAlta, nivelFinal);
            call.resolve();
        }
    }
}
