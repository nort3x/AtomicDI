package me.neiizun.lightdrop.lightdrop;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.LoggerFactory;


public class LightDrop extends ListenerAdapter {

    private JDA jda;

    /**
     * Hook LightDrop to your JDA instance.
     *
     * @param jda JDA instance.
     */
    public void hook(JDA jda) {
        jda.addEventListener(this);
        this.jda = jda;
        LoggerFactory.getLogger(LightDrop.class).info("Hooked into " + jda.getSelfUser().getName());
    }

}
