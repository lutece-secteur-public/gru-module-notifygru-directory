package fr.paris.lutece.plugins.notifygru.modules.directory.services.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryConstants;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.AbstractProviderManager;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;

public class DirectoryProviderManager extends AbstractProviderManager
{   
    @Inject
    private ActionService _actionService;
    
    public DirectoryProviderManager( String strId )
    {
        super( strId );
    }
    
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( ITask task )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );
        DirectoryFilter filter = new DirectoryFilter(  );
        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( Directory directory : listDirectory ) {
          Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
          Workflow wf = action.getWorkflow(  );

          if ( ( wf.getId( ) == directory.getIdWorkflow( ) ) ) {
            ProviderDescription providerDescription = 
                new ProviderDescription( String.valueOf( directory.getIdDirectory( ) ), 
                    I18nService.getLocalizedString( NotifyGruDirectoryConstants.TITLE_I18NKEY, I18nService.getDefaultLocale( ) ) + directory.getTitle( ) );
            collectionProviderDescriptions.add( providerDescription );
          }
        }
        
        return collectionProviderDescriptions;
    }

    @Override
    public ProviderDescription getProviderDescription( String strProviderId )
    {
        Directory directory = DirectoryHome.findByPrimaryKey( Integer.parseInt( strProviderId ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
        
        ProviderDescription providerDescription = 
                new ProviderDescription( String.valueOf( directory.getIdDirectory( ) ), 
                    I18nService.getLocalizedString( NotifyGruDirectoryConstants.TITLE_I18NKEY, I18nService.getDefaultLocale( ) ) + directory.getTitle( ) );
        
        providerDescription.setMarkerDescriptions( DirectoryProvider.getProviderMarkerDescriptions( strProviderId ) );
        
        return providerDescription;
    }

    @Override
    public IProvider createProvider( String strProviderId, ResourceHistory resourceHistory )
    {
        return new DirectoryProvider( getId( ), strProviderId, resourceHistory );
    }

}
