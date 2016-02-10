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
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryConstants;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;


// TODO: Auto-generated Javadoc
/**
 * The Class NotifyGruDirectory.
 */
public class NotifyGruDirectory extends AbstractServiceProvider
{
    
    /** The Constant TEMPLATE_FREEMARKER_LIST. */
    private static final String TEMPLATE_FREEMARKER_LIST = "admin/plugins/workflow/modules/notifygru/directory/freemarker_list.html";
    
    /** The _resource history service. */
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    
    /** The _provider directory service. */
    @Inject
    private INotifyGruDirectoryService _providerDirectoryService;

    /** The _n position user email. */
    //config provider
    private int _npositionUserEmail;
    
    /** The _n position user guid. */
    private int _npositionUserGuid;
    
    /** The _n position user phone number. */
    private int _npositionUserPhoneNumber;
    
    /** The _n position demand type. */
    private int _npositionDemandType;
    
    /** The _n id directory. */
    private int _nIdDirectory;
    
    /** The plugin directory. */
    private Plugin _pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserEmail(int)
     */
    @Override
    public String getUserEmail( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );

        return _providerDirectoryService.getEmail( _npositionUserEmail, record.getIdRecord(  ), _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getUserGuid(int)
     */
    @Override
    public String getUserGuid( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );

        return _providerDirectoryService.getUserGuid( _npositionUserGuid, record.getIdRecord(  ), _nIdDirectory );
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

        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                    TEMPLATE_FREEMARKER_LIST, local, model ).getHtml(  ), local, model );

        String strResourceInfo = t.getHtml(  );

        return strResourceInfo;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getInfos(int)
     */
    @Override
    public Map<String, Object> getInfos( int nIdResource )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( nIdResource > 0 )
        {
            ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );
            Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
            		_pluginDirectory );

            List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );

            String strValue;

            for ( IEntry recordField : listRecordField )
            {
                strValue = _providerDirectoryService.getRecordFieldValue( recordField.getPosition(  ),
                        record.getIdRecord(  ), _nIdDirectory );
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
    public String getOptionalMobilePhoneNumber( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );

        return _providerDirectoryService.getSMSPhoneNumber( _npositionUserPhoneNumber, record.getIdRecord(  ),
            _nIdDirectory );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandId(int)
     */
    @Override
    public int getOptionalDemandId( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );

        return record.getIdRecord(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.workflow.modules.notifygru.service.IProvider#getOptionalDemandIdType(int)
     */
    @Override
    public int getOptionalDemandIdType( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), _pluginDirectory );

        return _providerDirectoryService.getIdDemandType( _npositionDemandType, record.getIdRecord(  ), _nIdDirectory );
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
     * Gets the position demand type.
     *
     * @return the position demand type
     */
    public int getPositionDemandType(  )
    {
        return _npositionDemandType;
    }

    /**
     * Sets the position demand type.
     *
     * @param npositionDemandType the new position demand type
     */
    public void setPositionDemandType( int npositionDemandType )
    {
        this._npositionDemandType = npositionDemandType;
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

    @Override
    public String getDemandReference( int nIdResourceHistory ) 
    {
        return "Nothing";
    }

    @Override
    public String getCustomerId( int nIdResourceHistory ) 
    {
        return "Nothing";
    }
}
