/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.notifygru.modules.directory;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.IDemandTypeService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryConstants;
import fr.paris.lutece.plugins.workflow.business.action.ActionDAO;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;



/**
 * The Class NotifyGruDirectory.
 */
public final class NotifyGruDirectoryManager extends AbstractServiceProvider
{
    
    /** The _list provider notify gru directory. */
    private static Map<String, NotifyGruDirectoryManager> _listProviderNotifyGruDirectory;
    
    /** The _str key. */
    private static String _strKey = "notifygru-directory.ProviderService.@.";
    
    /** The Constant BEAN_SERVICE_DEMAND_TYPE. */
    private static final String BEAN_SERVICE_DEMAND_TYPE = "notifygru-directory.DefaultDemandTypeService";
    
    /** The _bean demand type service. */
    private static IDemandTypeService _beanDemandTypeService;

    /**
     * The Constant TEMPLATE_FREEMARKER_LIST.
     */
    private static final String TEMPLATE_FREEMARKER_LIST = "admin/plugins/workflow/modules/notifygru/directory/freemarker_list.html";

    /**
     * The _resource history service.
     */
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * The _provider directory service.
     */
    @Inject
    private INotifyGruDirectoryService _providerDirectoryService;
    
    /** The _action dao. */
    @Inject
    private ActionDAO _actionDAO;

    /**
     * The _n position user email.
     */

    //config provider
    private int _npositionUserEmail;

    /**
     * The _n position user guid.
     */
    private int _npositionUserGuid;
    
    /** The _nposition user cuid. */
    private int _npositionUserCuid;
    
    /** The _nposition demand reference. */
    private int _npositionDemandReference;

    /**
     * The _n position user phone number.
     */
    private int _npositionUserPhoneNumber;


    /**
     * The _n id directory.
     */
    private int _nIdDirectory;

    /**
     * The plugin directory.
     */
    private final Plugin _pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

    
    /** The _resource history. */
    private ResourceHistory _resourceHistory;
    
    /** The _record. */
    private Record _record;

    private NotifygruMappingManager _mapping;
    
    
 
    
    
    /**
     * Gets the record.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the record
     */
    private Record getRecord( int nIdResourceHistory )
    {
    	if( ( _record == null ) || (  _resourceHistory != null && nIdResourceHistory != _resourceHistory.getId( ) ) )
        {
    		_record = RecordHome.findByPrimaryKey( getResourceHistory( nIdResourceHistory ).getIdResource(  ), _pluginDirectory );
        }
    	
    	if( _record == null )
    	{    	
    		throw new AppException( "_record is NULL" );
    	}
    	return _record;
    }
    
    /**
     * Gets the resource history.
     *
     * @param nIdResourceHistory the n id resource history
     * @return the resource history
     */
    private ResourceHistory getResourceHistory( int nIdResourceHistory )
    {
    	if( ( _resourceHistory == null ) || (  _resourceHistory != null && nIdResourceHistory != _resourceHistory.getId( ) ) )
        {
        	_resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        }
    	
    	if( _resourceHistory == null )
    	{    	
    		throw new AppException( "Ressource History is NULL" );
    	}
    	return _resourceHistory;
    }
    
    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#isKeyProvider(java.lang.String)
     */
    @Override
    public Boolean isKeyProvider( String strKey )
    {
        return ( _listProviderNotifyGruDirectory == null ) ? false : _listProviderNotifyGruDirectory.containsKey( strKey );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#updateListProvider(fr.paris.lutece.plugins.workflowcore.service.task.ITask)
     */
    @Override
    public void updateListProvider( ITask task )
    {
        DirectoryFilter filter = new DirectoryFilter(  );

        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, _pluginDirectory );

        if ( _listProviderNotifyGruDirectory == null )
        {
            _listProviderNotifyGruDirectory = new HashMap<String, NotifyGruDirectoryManager>(  );
        }

        for ( Directory directory : listDirectory )
        {
            String strKeyProvider = _strKey + directory.getIdDirectory(  );
            String strBeanName = _strKey + directory.getIdDirectory(  );

            Action action = _actionDAO.load( task.getAction(  ).getId(  ) );

            Workflow wf = action.getWorkflow(  );

            if ( !_listProviderNotifyGruDirectory.containsKey( strKeyProvider ) && directory.isEnabled(  ) &&
                    ( wf.getId(  ) == directory.getIdWorkflow(  ) ) )
            {
                //   NotifyGruDirectory provider = new NotifyGruDirectory(strKeyProvider, strTitleI18nKey, strBeanName, position, directory.getIdDirectory());
                NotifyGruDirectoryManager provider = new NotifyGruDirectoryManager(  );
                provider._resourceHistoryService = _resourceHistoryService;
                provider._actionDAO = _actionDAO;
                provider._providerDirectoryService = _providerDirectoryService;

                provider.setBeanName( strBeanName );
                provider.setKey( strKeyProvider );
                provider.settitleI18nKey( NotifyGruDirectoryConstants.TITLE_I18NKEY );
                provider.setIdDirectory( directory.getIdDirectory(  ) );
                provider.setManagerProvider( true );
                
                
                NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strBeanName );
                
                if( mapping != null )
                {
                	 
                       provider.setPositionUserEmail( mapping.getEmail( ) );
                       provider.setPositionUserPhoneNumber( mapping.getMobilePhoneNumber( ) );
                       provider.setPositionUserGuid( 3 );                     
                       provider.setPositionUserCuid( 4 );
                       provider.setPositionDemandReference( 5 );
                }


                _listProviderNotifyGruDirectory.put( strKeyProvider, provider );
            }

            //
        }
    }

    
    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#updateListProvider()
     */
    @Override
    public void updateListProvider(  )
    {
        DirectoryFilter filter = new DirectoryFilter(  );

        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, _pluginDirectory );

        if ( _listProviderNotifyGruDirectory == null )
        {
            _listProviderNotifyGruDirectory = new HashMap<String, NotifyGruDirectoryManager>(  );
        }

        for ( Directory directory : listDirectory )
        {
            String strKeyProvider = _strKey + directory.getIdDirectory(  );
            String strBeanName = _strKey + directory.getIdDirectory(  );

          

            if ( !_listProviderNotifyGruDirectory.containsKey( strKeyProvider )   )
            {
                //   NotifyGruDirectory provider = new NotifyGruDirectory(strKeyProvider, strTitleI18nKey, strBeanName, position, directory.getIdDirectory());
                NotifyGruDirectoryManager provider = new NotifyGruDirectoryManager(  );
                provider._resourceHistoryService = _resourceHistoryService;
                provider._actionDAO = _actionDAO;
                provider._providerDirectoryService = _providerDirectoryService;

                provider.setBeanName( strBeanName );
                provider.setKey( strKeyProvider );
                provider.settitleI18nKey( NotifyGruDirectoryConstants.TITLE_I18NKEY );
                provider.setIdDirectory( directory.getIdDirectory(  ) );
                provider.setManagerProvider( true );

                NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( strBeanName );
                if( mapping != null )
                {
                	   provider.setPositionUserEmail( mapping.getEmail( ) );
                       provider.setPositionUserPhoneNumber( mapping.getMobilePhoneNumber( ) );
                       provider.setPositionUserGuid( 3 );                     
                       provider.setPositionUserCuid( 4 );
                       provider.setPositionDemandReference( 5 );
                }
                _listProviderNotifyGruDirectory.put( strKeyProvider, provider );
            }

            //
        }
    }
   
	/**
	 * Gets the position user cuid.
	 *
	 * @return the position user cuid
	 */
	public int getPositionUserCuid( ) 
	{
		return _npositionUserCuid;
	}


	/**
	 * Sets the position user cuid.
	 *
	 * @param positionUserCuid the new position user cuid
	 */
	public void setPositionUserCuid( int positionUserCuid ) 
	{
		this._npositionUserCuid = positionUserCuid;
	}

	
	/**
	 * Gets the position demand reference.
	 *
	 * @return the position demand reference
	 */
	public int getPositionDemandReference( ) 
	{
		return _npositionDemandReference;
	}

	
	/**
	 * Sets the position demand reference.
	 *
	 * @param positionDemandReference the new position demand reference
	 */
	public void setPositionDemandReference( int positionDemandReference ) 
	{
		this._npositionDemandReference = positionDemandReference;
	}

	/* (non-Javadoc)
	 * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#buildReferenteListProvider()
	 */
	@Override
    public ReferenceList buildReferenteListProvider(  )
    {
        ReferenceList refenreceList = new ReferenceList(  );

        for ( Map.Entry<String, NotifyGruDirectoryManager> entrySet : _listProviderNotifyGruDirectory.entrySet(  ) )
        {
            String key = entrySet.getKey(  );
            NotifyGruDirectoryManager provider = entrySet.getValue(  );

            Directory directory = DirectoryHome.findByPrimaryKey( provider.getIdDirectory(  ), _pluginDirectory );

            refenreceList.addItem( provider.getBeanName(  ),
                provider.getTitle( Locale.getDefault(  ) ) + " : " + directory.getTitle(  ) );
        }

        return refenreceList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#getInstanceProvider(java.lang.String)
     */
    @Override
    public AbstractServiceProvider getInstanceProvider( String strKey )
    {
        return _listProviderNotifyGruDirectory.get( strKey );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserEmail(int)
     */
    @Override
    public String getUserEmail( int nIdResourceHistory )
    {
           return _providerDirectoryService.getEmail( _npositionUserEmail, getRecord( nIdResourceHistory ).getIdRecord( ), _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserGuid(int)
     */
    @Override
    public String getUserGuid( int nIdResourceHistory )
    {
     return _providerDirectoryService.getUserGuid( _npositionUserGuid,getRecord( nIdResourceHistory ).getIdRecord( ), _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfosHelp(java.util.Locale)
     */
    @Override
    public String getInfosHelp( Locale local )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        List<IEntry> lrecord = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );
        model.put( NotifyGruDirectoryConstants.MARK_STATE_LIST_MARKER_RESSOURCE, lrecord );

        @SuppressWarnings( "deprecation" )
        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                    TEMPLATE_FREEMARKER_LIST, local, model ).getHtml(  ), local, model );

        String strResourceInfo = t.getHtml(  );

        return strResourceInfo;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfos(int)
     */
    @Override
    public Map<String, Object> getInfos( int nIdResourceHistory )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( nIdResourceHistory > 0 )
        {         
            List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );
            String strValue;
            for ( IEntry recordField : listRecordField )
            {
                strValue = _providerDirectoryService.getRecordFieldValue( recordField.getPosition(  ),
                		getRecord( nIdResourceHistory ).getIdRecord( ), _nIdDirectory );
                model.put( NotifyGruDirectoryConstants.MARK_POSITION + recordField.getPosition(  ), strValue );
            }
        }
        else
        {
            List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );

            for ( IEntry recordField : listRecordField )
            {
                model.put( NotifyGruDirectoryConstants.MARK_POSITION + recordField.getPosition(  ), "" );
            }
        }

        return model;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalMobilePhoneNumber(int)
     */
    @Override
    public String getOptionalMobilePhoneNumber( int nIdResourceHistory )
    {
      
        return _providerDirectoryService.getSMSPhoneNumber( _npositionUserPhoneNumber, getRecord( nIdResourceHistory ).getIdRecord( ),
            _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandId(int)
     */
    @Override
    public int getOptionalDemandId( int nIdResourceHistory )
    {
         return getRecord( nIdResourceHistory ).getIdRecord( );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandIdType(int)
     */
    @Override
    public int getOptionalDemandIdType( int nIdResource )
    {
        Directory directory = DirectoryHome.findByPrimaryKey( getIdDirectory(  ), _pluginDirectory );

           if ( _beanDemandTypeService == null )
           {
               _beanDemandTypeService = SpringContextService.getBean( BEAN_SERVICE_DEMAND_TYPE );
           }

           int nDemandType = _beanDemandTypeService.getDemandType( directory );
           AppLogService.info( "DemandTypeId : " + nDemandType );

           return nDemandType;
    }

    /**
     * Checks if is id demand type available.
     *
     * @param nIdResource the n id resource
     * @return the boolean
     */
    public Boolean isIdDemandTypeAvailable( int nIdResource )
    {
        return true;
    }

    /**
     * Gets the position user email.
     *
     * @return the position user email
     */
    public int getPositionUserEmail(  )
    {
        return _npositionUserEmail;
    }

    /**
     * Sets the position user email.
     *
     * @param npositionUserEmail the new position user email
     */
    public void setPositionUserEmail( int npositionUserEmail )
    {
        this._npositionUserEmail = npositionUserEmail;
    }

    /**
     * Gets the position user guid.
     *
     * @return the position user guid
     */
    public int getPositionUserGuid(  )
    {
        return _npositionUserGuid;
    }

    /**
     * Sets the position user guid.
     *
     * @param npositionUserGuid the new position user guid
     */
    public void setPositionUserGuid( int npositionUserGuid )
    {
        this._npositionUserGuid = npositionUserGuid;
    }

    /**
     * Gets the position user phone number.
     *
     * @return the position user phone number
     */
    public int getPositionUserPhoneNumber(  )
    {
        return _npositionUserPhoneNumber;
    }

    /**
     * Sets the position user phone number.
     *
     * @param npositionUserPhoneNumber the new position user phone number
     */
    public void setPositionUserPhoneNumber( int npositionUserPhoneNumber )
    {
        this._npositionUserPhoneNumber = npositionUserPhoneNumber;
    }

   

    /**
     * Gets the id directory.
     *
     * @return the id directory
     */
    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    /**
     * Sets the id directory.
     *
     * @param nIdDirectory the new id directory
     */
    public void setIdDirectory( int nIdDirectory )
    {
        this._nIdDirectory = nIdDirectory;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getDemandReference(int)
     */
    @Override
    public String getDemandReference( int nIdResourceHistory )
    {
    	   return _providerDirectoryService.getRecordFieldValue( _npositionDemandReference, getRecord( nIdResourceHistory ).getIdRecord( ), _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getCustomerId(int)
     */
    @Override
    public String getCustomerId( int nIdResourceHistory )
    {    	
         return _providerDirectoryService.getRecordFieldValue( _npositionUserCuid, getRecord( nIdResourceHistory ).getIdRecord( ), _nIdDirectory );
    }

	/* (non-Javadoc)
	 * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider#getReferenteListEntityProvider()
	 */
	@Override
	public ReferenceList getReferenteListEntityProvider()
	{
			
		ReferenceList refenreceList = new ReferenceList(  );      
        List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );
          for ( IEntry recordField : listRecordField )        	  
          {
        	   refenreceList.addItem( recordField.getPosition(), recordField.getTitle() );
          }          
          return refenreceList;
	}



}
