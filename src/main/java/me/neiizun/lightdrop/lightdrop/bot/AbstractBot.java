package me.neiizun.lightdrop.lightdrop.bot;

import net.dv8tion.jda.api.JDA;

public abstract class AbstractBot extends BotWrapper {
    AbstractBot(){
        super();
        super.setInnerJDA(provideConfiguredJDA());
    }

    abstract JDA provideConfiguredJDA();
    abstract String provideName();

}
