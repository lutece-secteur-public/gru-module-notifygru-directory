package fr.paris.lutece.plugins.notifygru.modules.directory.services.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.modules.mappingmanager.business.DirectoryMappingManager;
import fr.paris.lutece.plugins.directory.modules.mappingmanager.business.DirectoryMappingManagerHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.IDemandTypeService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryConstants;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;

public class DirectoryProvider implements IProvider
{
    // Beans
    private static final String BEAN_SERVICE_DEMAND_TYPE = "notifygru-directory.DefaultDemandTypeService";

    private static INotifyGruDirectoryService _notifyGruDirectoryService = SpringContextService
            .getBean( NotifyGruDirectoryConstants.BEAN_SERVICE_PROVIDER_DIRECTORY );

    private String _strCustomerEmail;

    private String _strCustomerConnectionId;

    private String _strCustomerId;

    private String _strCustomerPhoneNumber;

    private String _strDemandReference;

    private String _strDemandTypeId;

    private Directory _directory;

    private Record _record;

    public DirectoryProvider( String strProviderManagerId, String strProviderId, ResourceHistory resourceHistory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        _directory = DirectoryHome.findByPrimaryKey( Integer.parseInt( strProviderId ), pluginDirectory );
        _record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource( ), pluginDirectory );

        IDemandTypeService demandTypeService = SpringContextService.getBean( BEAN_SERVICE_DEMAND_TYPE );

        DirectoryMappingManager mapping = DirectoryMappingManagerHome.findByPrimaryKey( ProviderManagerUtil.buildCompleteProviderId( strProviderManagerId,
                strProviderId ) );

        if ( mapping == null )
        {
            throw new AppException( "No mapping found for the directory " + _directory.getTitle( )
                    + ". Please check the configuration of the module-directory-mappingmanager." );
        }

        _strCustomerEmail = _notifyGruDirectoryService.getEmail( mapping.getEmail( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerConnectionId = _notifyGruDirectoryService.getUserGuid( mapping.getGuid( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerId = _notifyGruDirectoryService.getRecordFieldValue( mapping.getCustomerId( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerPhoneNumber = _notifyGruDirectoryService.getSMSPhoneNumber( mapping.getMobilePhoneNumber( ), _record.getIdRecord( ),
                _directory.getIdDirectory( ) );
        _strDemandReference = _notifyGruDirectoryService.getRecordFieldValue( mapping.getReferenceDemand( ), _record.getIdRecord( ),
                _directory.getIdDirectory( ) )
                + "-" + _record.getIdRecord( );
        _strDemandTypeId = String.valueOf( demandTypeService.getDemandType( _directory ) );
    }

    @Override
    public String provideDemandId( )
    {
        return String.valueOf( _record.getIdRecord( ) );
    }

    @Override
    public String provideDemandTypeId( )
    {
        return _strDemandTypeId;
    }

    @Override
    public String provideDemandReference( )
    {
        return _strDemandReference;
    }

    @Override
    public String provideCustomerConnectionId( )
    {
        return _strCustomerConnectionId;
    }

    @Override
    public String provideCustomerId( )
    {
        return _strCustomerId;
    }

    @Override
    public String provideCustomerEmail( )
    {
        return _strCustomerEmail;
    }

    @Override
    public String provideCustomerMobilePhone( )
    {
        return _strCustomerPhoneNumber;
    }

    @Override
    public Collection<NotifyGruMarker> provideMarkerValues( )
    {
        Collection<NotifyGruMarker> result = new ArrayList<>( );

        List<IEntry> listRecordField = _notifyGruDirectoryService.getEntries( _directory.getIdDirectory( ) );

        for ( IEntry recordField : listRecordField )
        {
            NotifyGruMarker notifyGruMarker = new NotifyGruMarker( NotifyGruDirectoryConstants.MARK_POSITION + recordField.getPosition( ) );
            notifyGruMarker.setValue( _notifyGruDirectoryService.getRecordFieldValue( recordField.getPosition( ), _record.getIdRecord( ),
                    _directory.getIdDirectory( ) ) );

            result.add( notifyGruMarker );
        }

        return result;
    }

    public static Collection<NotifyGruMarker> getProviderMarkerDescriptions( String strProviderId )
    {
        Collection<NotifyGruMarker> collectionNotifyGruMarkers = new ArrayList<>( );

        List<IEntry> listEntries = _notifyGruDirectoryService.getEntries( Integer.parseInt( strProviderId ) );

        for ( IEntry entry : listEntries )
        {
            NotifyGruMarker notifyGruMarker = new NotifyGruMarker( NotifyGruDirectoryConstants.MARK_POSITION + entry.getPosition( ) );
            notifyGruMarker.setDescription( entry.getTitle( ) );

            collectionNotifyGruMarkers.add( notifyGruMarker );
        }

        return collectionNotifyGruMarkers;
    }

}
