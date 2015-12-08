
package fr.paris.lutece.plugins.gru.modules.provider.directory.implementation;

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginDefaultImplementation;
import fr.paris.lutece.portal.service.plugin.PluginService;


/**
 *
 * ProviderDirectoryPlugin
 *
 */
public class ProviderDirectoryPlugin extends PluginDefaultImplementation
{
    public static final String PLUGIN_NAME = "notifygru-providerdirectory";
    public static final String BEAN_TRANSACTION_MANAGER = PLUGIN_NAME + ".transactionManager";

    /**
     * Get the plugin
     * @return the plugin
     */
    public static Plugin getPlugin(  )
    {
        return PluginService.getPlugin( PLUGIN_NAME );
    }
}
