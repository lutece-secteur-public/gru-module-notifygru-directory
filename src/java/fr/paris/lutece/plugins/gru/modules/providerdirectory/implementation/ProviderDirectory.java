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
package fr.paris.lutece.plugins.gru.modules.providerdirectory.implementation;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.AbstractServiceProvider;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.plugins.workflowcore.service.state.StateService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;


public class ProviderDirectory extends AbstractServiceProvider
{
    private static final String TEMPLATE_FREEMARKER_LIST = "admin/plugins/workflow/modules/notifygru/providerdirectory/freemarker_list.html";
    @Inject
    private IResourceHistoryService _resourceHistoryService;
    @Inject
    private IProviderDirectoryService _providerDirectoryService;
    @Inject
    @Named( StateService.BEAN_SERVICE )
    private IStateService _taskStateService;

    //config provider
    private int _nPositionUserEmail;
    private int _nPositionUserGuid;
    private int _nPositionUserPhoneNumber;
    private int _nPositionDemand;
    private int _nPositionDemandType;
    private int _nIdDirectory;
    private int _nIdStatusCRM;
    private String _strStatusTexte;
    private Boolean _bUserPhoneNumberAvailable;
    private Boolean _bIdDemandAvailable;
    private Boolean _bIdDemandTypeAvailable;
    Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

    @Override
    public String getUserEmail( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        return _providerDirectoryService.getEmail( _nPositionUserEmail, record.getIdRecord(  ), _nIdDirectory );
    }

    @Override
    public String getUserGuid( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        return _providerDirectoryService.getUserGuid( _nPositionUserGuid, record.getIdRecord(  ), _nIdDirectory );
    }

    @Override
    public String getStatus( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
        Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                pluginDirectory );
        Action action = resourceHistory.getAction(  );

        //  State stateBefore = _taskStateService.findByPrimaryKey(action.getStateBefore().getId());
        State stateAfter = _taskStateService.findByPrimaryKey( action.getStateAfter(  ).getId(  ) );

        //State state= WorkflowService.getInstance(  ).getState( nIdResource, WORKFLOW_RESOURCE_TYPE, directory.getIdWorkflow(), -1 );        
        return stateAfter.getName(  );
    }

    @Override
    public String getInfosHelp( Locale local )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        List<IEntry> lrecord =_providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );
        model.put( "list_entries",  lrecord);

        HtmlTemplate t = AppTemplateService.getTemplateFromStringFtl( AppTemplateService.getTemplate( 
                    TEMPLATE_FREEMARKER_LIST, local, model ).getHtml(  ), local, model );

        String strResourceInfo = t.getHtml(  );

        return strResourceInfo;
    }

    @Override
    public Object getInfos( int nIdResource )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );

        if ( nIdResource > 0 )
        {
            ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
            Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );
            Directory directory = DirectoryHome.findByPrimaryKey( record.getDirectory(  ).getIdDirectory(  ),
                    pluginDirectory );

            List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );

            String strValue;

            for ( IEntry recordField : listRecordField )
            {
                strValue = _providerDirectoryService.getRecordFieldValue( recordField.getPosition(  ),
                        record.getIdRecord(  ), _nIdDirectory );
                model.put( "position_" + recordField.getPosition(  ), strValue );
            }
        }
        else
        {
            List<IEntry> listRecordField = _providerDirectoryService.getListEntriesFreemarker( _nIdDirectory );

            for ( IEntry recordField : listRecordField )
            {
                model.put( "position_" + recordField.getPosition(  ), "" );
            }
        }

        return model;
    }

    @Override
    public String getMobilePhoneNumber( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        return _providerDirectoryService.getSMSPhoneNumber( _nPositionUserPhoneNumber, record.getIdRecord(  ),
            _nIdDirectory );
    }

    @Override
    public Boolean isMobilePhoneNumberAvailable( int nIdResource )
    {
        return true;
    }

    @Override
    public int getIdDemand( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        return _providerDirectoryService.getIdDemand( _nPositionDemand, record.getIdRecord(  ), _nIdDirectory );
    }

    @Override
    public Boolean isIdDemandAvailable( int nIdResource )
    {
        return true;
    }

    @Override
    public int getIdDemandType( int nIdResource )
    {
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResource );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        return _providerDirectoryService.getIdDemandType( _nPositionDemandType, record.getIdRecord(  ), _nIdDirectory );
    }

    @Override
    public Boolean isIdDemandTypeAvailable( int nIdResource )
    {
        return true;
    }

    public int getPositionUserEmail(  )
    {
        return _nPositionUserEmail;
    }

    public void setPositionUserEmail( int _nPositionUserEmail )
    {
        this._nPositionUserEmail = _nPositionUserEmail;
    }

    public int getPositionUserGuid(  )
    {
        return _nPositionUserGuid;
    }

    public void setPositionUserGuid( int _nPositionUserGuid )
    {
        this._nPositionUserGuid = _nPositionUserGuid;
    }

    public int getPositionUserPhoneNumber(  )
    {
        return _nPositionUserPhoneNumber;
    }

    public void setPositionUserPhoneNumber( int _nPositionUserPhoneNumber )
    {
        this._nPositionUserPhoneNumber = _nPositionUserPhoneNumber;
    }

    public int getPositionDemand(  )
    {
        return _nPositionDemand;
    }

    public void setPositionDemand( int _nPositionDemand )
    {
        this._nPositionDemand = _nPositionDemand;
    }

    public int getPositionDemandType(  )
    {
        return _nPositionDemandType;
    }

    public void setPositionDemandType( int _nPositionDemandType )
    {
        this._nPositionDemandType = _nPositionDemandType;
    }

    public int getIdDirectory(  )
    {
        return _nIdDirectory;
    }

    public void setIdDirectory( int _nIdDirectory )
    {
        this._nIdDirectory = _nIdDirectory;
    }

    public Boolean IsUserPhoneNumberAvailable(  )
    {
        return _bUserPhoneNumberAvailable;
    }

    public void setbUserPhoneNumberAvailable( Boolean _bUserPhoneNumberAvailable )
    {
        this._bUserPhoneNumberAvailable = _bUserPhoneNumberAvailable;
    }

    public Boolean iSbIdDemandAvailable(  )
    {
        return _bIdDemandAvailable;
    }

    public void setbIdDemandAvailable( Boolean _bIdDemandAvailable )
    {
        this._bIdDemandAvailable = _bIdDemandAvailable;
    }

    public Boolean iSbIdDemandTypeAvailable(  )
    {
        return _bIdDemandTypeAvailable;
    }

    public void setbIdDemandTypeAvailable( Boolean _bIdDemandTypeAvailable )
    {
        this._bIdDemandTypeAvailable = _bIdDemandTypeAvailable;
    }

    public int getIdStatusCRM(  )
    {
        return _nIdStatusCRM;
    }

    public void setIdStatusCRM( int _nIdStatusCRM )
    {
        this._nIdStatusCRM = _nIdStatusCRM;
    }

    public String getStatusTexte(  )
    {
        return _strStatusTexte;
    }

    public void setStatusTexte( String _strStatusTexte )
    {
        this._strStatusTexte = _strStatusTexte;
    }
}
