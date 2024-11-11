package client;

import client.scenes.*;
import client.utils.CurrencyExchangeService;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

/**
 * MyModule class
 */
public class MyModule implements Module {
    /**
     * Contributes bindings and other configurations for this module to {@code binder}.
     *
     * @param binder
     */
    @Override
    public void configure(Binder binder) {
        binder.bind(MainCtrl.class).in(Singleton.class);
        binder.bind(OverviewCtrl.class).in(Singleton.class);
        binder.bind(StartCtrl.class).in(Singleton.class);
        binder.bind(AddEditParticipantCtrl.class).in(Singleton.class);
        binder.bind(DebtCtrl.class).in(Singleton.class);
        binder.bind(InviteParticipantCtrl.class).in(Singleton.class);
        binder.bind(LanguageCtrl.class).in(Singleton.class);
        binder.bind(SettingsCtrl.class).in(Singleton.class);
        binder.bind(AddExpenseCtrl.class).in(Singleton.class);
        binder.bind(CurrencyExchangeService.class).in(Singleton.class);
    }
}
