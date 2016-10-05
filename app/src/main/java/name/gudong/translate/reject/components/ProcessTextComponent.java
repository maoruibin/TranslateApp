package name.gudong.translate.reject.components;

import dagger.Component;
import name.gudong.translate.reject.ActivityScope;
import name.gudong.translate.reject.modules.TipViewModule;
import name.gudong.translate.ui.activitys.ProcessTextActivity;

/**
 * Created by GuDong on 10/5/16 16:33.
 * Contact with gudong.name@gmail.com.
 */

@ActivityScope
@Component(modules = {TipViewModule.class},dependencies = {AppComponent.class})
public interface ProcessTextComponent {
    void inject(ProcessTextActivity activity);
}

