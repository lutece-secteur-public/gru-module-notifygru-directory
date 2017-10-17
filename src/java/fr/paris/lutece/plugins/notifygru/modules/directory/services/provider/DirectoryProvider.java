/*
 * Copyright (c) 2002-2017, Mairie de Paris
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
package fr.paris.lutece.plugins.notifygru.modules.directory.services.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManager;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.business.NotifygruMappingManagerHome;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryService;
import fr.paris.lutece.plugins.workflow.modules.comment.business.CommentValue;
import fr.paris.lutece.plugins.workflow.modules.comment.service.CommentValueService;
import fr.paris.lutece.plugins.workflow.modules.comment.service.ICommentValueService;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.ProviderManagerUtil;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import fr.paris.lutece.plugins.workflowcore.service.task.TaskService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;

/**
 * This class represents a provider for a {@link Directory} object
 *
 */
public class DirectoryProvider implements IProvider
{
    // PROPERTY KEY
    private static final String PROPERTY_SMS_SENDER_NAME = "workflow-notifygrudirectory.gruprovider.sms.sendername";

    // MARKS
    private static final String MARK_POSITION = "position_";
    private static final String MARK_COMMENTS = "comments";

    // MESSAGE
    private static final String MESS_DESC_COMMENTS = "module.notifygru.directory.label_desc_comments";

    private static INotifyGruDirectoryService _notifyGruDirectoryService = SpringContextService.getBean( NotifyGruDirectoryService.BEAN_SERVICE );

    private final String _strCustomerEmail;

    private final String _strCustomerConnectionId;

    private final String _strCustomerId;

    private final String _strCustomerPhoneNumber;

    private final String _strDemandReference;

    private final String _strDemandTypeId;

    private String _strAllComments;

    private final Directory _directory;

    private final Record _record;

    /**
     * Constructor
     * 
     * @param strProviderManagerId
     *            the provider manager id. Used to retrieve the mapping.
     * @param strProviderId
     *            the provider id. Corresponds to the {@code Directory} id. Used to retrieve the mapping.
     * @param resourceHistory
     *            the resource history. Corresponds to the {@link Record} object containing the data to provide
     */
    public DirectoryProvider( String strProviderManagerId, String strProviderId, ResourceHistory resourceHistory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        _directory = DirectoryHome.findByPrimaryKey( Integer.parseInt( strProviderId ), pluginDirectory );
        _record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource( ), pluginDirectory );

        NotifygruMappingManager mapping = NotifygruMappingManagerHome.findByPrimaryKey( ProviderManagerUtil.buildCompleteProviderId( strProviderManagerId,
                strProviderId ) );

        if ( mapping == null )
        {
            throw new AppException( "No mapping found for the directory " + _directory.getTitle( )
                    + ". Please check the configuration of the module-directory-mappingmanager." );
        }

        _strCustomerEmail = _notifyGruDirectoryService.getEmail( mapping.getEmail( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerConnectionId = _notifyGruDirectoryService.getUserGuid( mapping.getConnectionId( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerId = _notifyGruDirectoryService.getRecordFieldValue( mapping.getCustomerId( ), _record.getIdRecord( ), _directory.getIdDirectory( ) );
        _strCustomerPhoneNumber = _notifyGruDirectoryService.getSMSPhoneNumber( mapping.getMobilePhoneNumber( ), _record.getIdRecord( ),
                _directory.getIdDirectory( ) );
        _strDemandReference = _notifyGruDirectoryService.getRecordFieldValue( mapping.getDemandReference( ), _record.getIdRecord( ),
                _directory.getIdDirectory( ) )
                + "-" + _record.getIdRecord( );
        _strDemandTypeId = String.valueOf( mapping.getDemandeTypeId( ) );

        // we retrieve all TaskComment of the action for the 'comments'
        // Only TaskComment declared BEFORE the TaskNotifyGru are set with value !
        ICommentValueService commentService = SpringContextService.getBean( CommentValueService.BEAN_SERVICE );
        ITaskService taskService = SpringContextService.getBean( TaskService.BEAN_SERVICE );
        _strAllComments = StringUtils.EMPTY;
        if ( commentService != null && taskService != null )
        {
            List<ITask> tasks = taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), Locale.getDefault( ) );
            StringBuilder strAllComments = new StringBuilder( );
            for ( ITask iTask : tasks )
            {
                CommentValue commentValue = commentService.findByPrimaryKey( resourceHistory.getId( ), iTask.getId( ),
                        PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );
                if ( commentValue != null )
                {
                    strAllComments.append( commentValue.getValue( ) );
                }
            }
            _strAllComments = strAllComments.toString( );
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandId( )
    {
        return String.valueOf( _record.getIdRecord( ) );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandTypeId( )
    {
        return _strDemandTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandSubtypeId( )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideDemandReference( )
    {
        return _strDemandReference;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerConnectionId( )
    {
        return _strCustomerConnectionId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerId( )
    {
        return _strCustomerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerEmail( )
    {
        return _strCustomerEmail;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideSmsSender( )
    {
        return AppPropertiesService.getProperty( PROPERTY_SMS_SENDER_NAME );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String provideCustomerMobilePhone( )
    {
        return _strCustomerPhoneNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotifyGruMarker> provideMarkerValues( )
    {
        Collection<NotifyGruMarker> result = new ArrayList<>( );

        List<IEntry> listRecordField = _notifyGruDirectoryService.getEntries( _directory.getIdDirectory( ) );

        for ( IEntry recordField : listRecordField )
        {
            NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_POSITION + recordField.getPosition( ) );
            notifyGruMarker.setValue( _notifyGruDirectoryService.getRecordFieldValue( recordField.getPosition( ), _record.getIdRecord( ),
                    _directory.getIdDirectory( ) ) );

            result.add( notifyGruMarker );
        }
        NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_COMMENTS );
        notifyGruMarker.setValue( _strAllComments );
        result.add( notifyGruMarker );

        return result;
    }

    /**
     * Gives the marker descriptions of the specified provider
     * 
     * @param strProviderId
     *            the provider id. Corresponds to the {@code Directory} id
     * @return the marker descriptions
     */
    public static Collection<NotifyGruMarker> getProviderMarkerDescriptions( String strProviderId )
    {
        Collection<NotifyGruMarker> collectionNotifyGruMarkers = new ArrayList<>( );

        List<IEntry> listEntries = _notifyGruDirectoryService.getEntries( Integer.parseInt( strProviderId ) );

        for ( IEntry entry : listEntries )
        {
            NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_POSITION + entry.getPosition( ) );
            notifyGruMarker.setDescription( entry.getTitle( ) );

            collectionNotifyGruMarkers.add( notifyGruMarker );
        }
        NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_COMMENTS );
        notifyGruMarker.setDescription( I18nService.getLocalizedString( MESS_DESC_COMMENTS, Locale.getDefault( ) ) );
        collectionNotifyGruMarkers.add( notifyGruMarker );

        return collectionNotifyGruMarkers;
    }

    /**
     * Gives a {@code ReferenceList} object containing the list of entries for the specified directory. The code of the {@code ReferenceItem} corresponds to the
     * position of the entry. The name of the {@code ReferenceItem} corresponds to the title of the entry.
     * 
     * @param strProviderId
     *            the provider id. Corresponds to the {@code Directory} id
     * @return the entries position
     */
    public static ReferenceList getEntryPositions( String strProviderId )
    {
        ReferenceList referenceList = new ReferenceList( );

        List<IEntry> listRecordField = _notifyGruDirectoryService.getEntries( Integer.parseInt( strProviderId ) );

        for ( IEntry entry : listRecordField )
        {
            ReferenceItem referenceItem = new ReferenceItem( );
            referenceItem.setCode( String.valueOf( entry.getPosition( ) ) );
            referenceItem.setName( entry.getTitle( ) );
            referenceList.add( referenceItem );
        }

        return referenceList;
    }

}
