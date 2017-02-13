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
package fr.paris.lutece.plugins.notifygru.modules.directory.services;

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.service.security.IWorkflowUserAttributesManager;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.business.state.StateFilter;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.state.IStateService;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mailinglist.AdminMailingListService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.security.LuteceUser;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;

import javax.servlet.http.HttpServletRequest;


/**
 * NotifyDirectoryService.
 */
public final class NotifyGruDirectoryService implements INotifyGruDirectoryService
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "notifygru-directory.ProviderDirectoryService";

    /** The _action service. */
    // SERVICES
    @Inject
    private IActionService _actionService;

    /** The _state service. */
    @Inject
    private IStateService _stateService;

    /** The _user attributes manager. */
    @Inject
    private IWorkflowUserAttributesManager _userAttributesManager;

    /** The _list accepted entry types email sms. */
    private final List<Integer> _listAcceptedEntryTypesEmailSMS;

    /** The _list accepted entry types user guid. */
    private final List<Integer> _listAcceptedEntryTypesUserGuid;

    /** The _list refused entry types. */
    private final List<Integer> _listRefusedEntryTypes;

    /** The _list accepted entry types file. */
    private final List<Integer> _listAcceptedEntryTypesFile;

    /**
     * Instantiates a new notify gru directory service.
     */
    private NotifyGruDirectoryService(  )
    {
        // Init list accepted entry types for email
        _listAcceptedEntryTypesEmailSMS = fillListEntryTypes( NotifyGruDirectoryConstants.PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_EMAIL_SMS );

        // Init list accepted entry types for user guid
        _listAcceptedEntryTypesUserGuid = fillListEntryTypes( NotifyGruDirectoryConstants.PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_USER_GUID );

        // Init list accepted entry types for file
        _listAcceptedEntryTypesFile = fillListEntryTypes( NotifyGruDirectoryConstants.PROPERTY_ACCEPTED_DIRECTORY_ENTRY_TYPE_FILE );

        // Init list refused entry types
        _listRefusedEntryTypes = fillListEntryTypes( NotifyGruDirectoryConstants.PROPERTY_REFUSED_DIRECTORY_ENTRY_TYPE_FOR_PROVIDER_FLUX_JSON );
    }

    /**
     * Fill list entry types.
     *
     * @param strPropertyEntryTypes the str property entry types
     * @return the list
     */
    private static List<Integer> fillListEntryTypes( String strPropertyEntryTypes )
    {
        List<Integer> listEntryTypes = new ArrayList<Integer>(  );
        String strEntryTypes = AppPropertiesService.getProperty( strPropertyEntryTypes );

        if ( StringUtils.isNotBlank( strEntryTypes ) )
        {
            String[] listAcceptEntryTypesForIdDemand = strEntryTypes.split( NotifyGruDirectoryConstants.COMMA );

            for ( String strAcceptEntryType : listAcceptEntryTypesForIdDemand )
            {
                if ( StringUtils.isNotBlank( strAcceptEntryType ) && StringUtils.isNumeric( strAcceptEntryType ) )
                {
                    int nAcceptedEntryType = Integer.parseInt( strAcceptEntryType );
                    listEntryTypes.add( nAcceptedEntryType );
                }
            }
        }

        return listEntryTypes;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#isEntryTypeEmailSMSAccepted(int)
     */
    @Override
    public boolean isEntryTypeEmailSMSAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesEmailSMS != null ) && !_listAcceptedEntryTypesEmailSMS.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesEmailSMS.contains( nIdEntryType );
        }

        return bIsAccepted;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#isEntryTypeUserGuidAccepted(int)
     */
    @Override
    public boolean isEntryTypeUserGuidAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesUserGuid != null ) && !_listAcceptedEntryTypesUserGuid.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesUserGuid.contains( nIdEntryType );
        }

        return bIsAccepted;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#isEntryTypeFileAccepted(int)
     */
    @Override
    public boolean isEntryTypeFileAccepted( int nIdEntryType )
    {
        boolean bIsAccepted = false;

        if ( ( _listAcceptedEntryTypesFile != null ) && !_listAcceptedEntryTypesFile.isEmpty(  ) )
        {
            bIsAccepted = _listAcceptedEntryTypesFile.contains( nIdEntryType );
        }

        return bIsAccepted;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#isEntryTypeRefused(int)
     */
    @Override
    public boolean isEntryTypeRefused( int nIdEntryType )
    {
        boolean bIsRefused = true;

        if ( ( _listRefusedEntryTypes != null ) && !_listRefusedEntryTypes.isEmpty(  ) )
        {
            bIsRefused = _listRefusedEntryTypes.contains( nIdEntryType );
        }

        return bIsRefused;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListStates(int)
     */
    @Override
    public ReferenceList getListStates( int nIdAction )
    {
        ReferenceList referenceListStates = new ReferenceList(  );
        Action action = _actionService.findByPrimaryKey( nIdAction );

        if ( ( action != null ) && ( action.getWorkflow(  ) != null ) )
        {
            StateFilter stateFilter = new StateFilter(  );
            stateFilter.setIdWorkflow( action.getWorkflow(  ).getId(  ) );

            List<State> listStates = _stateService.getListStateByFilter( stateFilter );

            referenceListStates.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );
            referenceListStates.addAll( ReferenceList.convert( listStates, NotifyGruDirectoryConstants.ID,
                    NotifyGruDirectoryConstants.NAME, true ) );
        }

        return referenceListStates;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListDirectories()
     */
    @Override
    public ReferenceList getListDirectories(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList listDirectories = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList refenreceListDirectories = new ReferenceList(  );
        refenreceListDirectories.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        if ( listDirectories != null )
        {
            refenreceListDirectories.addAll( listDirectories );
        }

        return refenreceListDirectories;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getMailingList(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public ReferenceList getMailingList( HttpServletRequest request )
    {
        ReferenceList refMailingList = new ReferenceList(  );
        refMailingList.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );
        refMailingList.addAll( AdminMailingListService.getMailingLists( AdminUserService.getAdminUser( request ) ) );

        return refMailingList;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntries(int)
     */
    @Override
    public List<IEntry> getListEntries( int nidDirectory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntries = new ArrayList<IEntry>(  );
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setIdDirectory( nidDirectory );

        listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        return listEntries;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntriesUserGuid(int, java.util.Locale)
     */
    @Override
    public ReferenceList getListEntriesUserGuid( int nidDirectory, Locale locale )
    {
        ReferenceList refenreceListEntries = new ReferenceList(  );
        refenreceListEntries.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        for ( IEntry entry : getListEntries( nidDirectory ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeUserGuidAccepted( nIdEntryType ) )
            {
                refenreceListEntries.addItem( entry.getPosition(  ), buildReferenceEntryToString( entry, locale ) );
            }
        }

        return refenreceListEntries;
    }

    /**
     * Builds the reference entry to string.
     *
     * @param entry the entry
     * @param locale the locale
     * @return the string
     */
    private String buildReferenceEntryToString( IEntry entry, Locale locale )
    {
        StringBuilder sbReferenceEntry = new StringBuilder(  );
        sbReferenceEntry.append( entry.getPosition(  ) );
        sbReferenceEntry.append( NotifyGruDirectoryConstants.SPACE + NotifyGruDirectoryConstants.OPEN_BRACKET );
        sbReferenceEntry.append( entry.getTitle(  ) );
        sbReferenceEntry.append( NotifyGruDirectoryConstants.SPACE + NotifyGruDirectoryConstants.HYPHEN +
            NotifyGruDirectoryConstants.SPACE );
        sbReferenceEntry.append( I18nService.getLocalizedString( entry.getEntryType(  ).getTitleI18nKey(  ), locale ) );
        sbReferenceEntry.append( NotifyGruDirectoryConstants.CLOSED_BRACKET );

        return sbReferenceEntry.toString(  );
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntriesEmailSMS(int, java.util.Locale)
     */
    @Override
    public ReferenceList getListEntriesEmailSMS( int nidDirectory, Locale locale )
    {
        ReferenceList refenreceListEntries = new ReferenceList(  );
        refenreceListEntries.addItem( DirectoryUtils.CONSTANT_ID_NULL, DirectoryUtils.EMPTY_STRING );

        for ( IEntry entry : getListEntries( nidDirectory ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeEmailSMSAccepted( nIdEntryType ) )
            {
                refenreceListEntries.addItem( entry.getPosition(  ), buildReferenceEntryToString( entry, locale ) );
            }
        }

        return refenreceListEntries;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntriesFreemarker(int)
     */
    @Override
    public List<IEntry> getListEntriesFreemarker( int nidDirectory )
    {
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        for ( IEntry entry : getListEntries( nidDirectory ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( !isEntryTypeRefused( nIdEntryType ) )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntriesFile(int, java.util.Locale)
     */
    @Override
    public List<IEntry> getListEntriesFile( int nidDirectory, Locale locale )
    {
        List<IEntry> listEntries = new ArrayList<IEntry>(  );

        for ( IEntry entry : getListEntries( nidDirectory ) )
        {
            int nIdEntryType = entry.getEntryType(  ).getIdType(  );

            if ( isEntryTypeFileAccepted( nIdEntryType ) )
            {
                listEntries.add( entry );
            }
        }

        return listEntries;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getEmail(int, int, int)
     */
    @Override
    public String getEmail( int nPositionEmail, int nIdRecord, int nIdDirectory )
    {
        String strEmail = StringUtils.EMPTY;

        strEmail = getRecordFieldValue( nPositionEmail, nIdRecord, nIdDirectory );

        return strEmail;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getIdDemand(int, int, int)
     */
    @Override
    public int getIdDemand( int nPositionDemand, int nIdRecord, int nIdDirectory )
    {
        String strIdDemand = StringUtils.EMPTY;

        strIdDemand = getRecordFieldValue( nPositionDemand, nIdRecord, nIdDirectory );

        int nId = -1;

        try
        {
            nId = Integer.parseInt( strIdDemand );
        }
        catch ( NumberFormatException e )
        {
        }

        return nId;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getIdDemandType(int, int, int)
     */
    @Override
    public int getIdDemandType( int nPositionDemandType, int nIdRecord, int nIdDirectory )
    {
        String strIdDemandType = StringUtils.EMPTY;

        strIdDemandType = getRecordFieldValue( nPositionDemandType, nIdRecord, nIdDirectory );

        int nId = -1;

        try
        {
            nId = Integer.parseInt( strIdDemandType );
        }
        catch ( NumberFormatException e )
        {
        }

        return nId;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getRecordFieldValue(int, int, int)
     */
    @Override
    public String getRecordFieldValue( int nPosition, int nIdRecord, int nIdDirectory )
    {
        String strRecordFieldValue = StringUtils.EMPTY;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        // RecordField
        EntryFilter entryFilter = new EntryFilter(  );
        entryFilter.setPosition( nPosition );
        entryFilter.setIdDirectory( nIdDirectory );

		List<IEntry> listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        if ( ( listEntries != null ) && !listEntries.isEmpty(  ) )
        {
            IEntry entry = listEntries.get( 0 );
            RecordFieldFilter recordFieldFilterEmail = new RecordFieldFilter(  );
            recordFieldFilterEmail.setIdDirectory( nIdDirectory );
            recordFieldFilterEmail.setIdEntry( entry.getIdEntry(  ) );
            recordFieldFilterEmail.setIdRecord( nIdRecord );

            List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilterEmail,
                    pluginDirectory );

            if ( ( listRecordFields != null ) && !listRecordFields.isEmpty(  ) && ( listRecordFields.get( 0 ) != null ) )
            {
                RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                strRecordFieldValue = recordFieldIdDemand.getValue(  );
                
                if ( recordFieldIdDemand.getField(  ) != null )
                {
                    strRecordFieldValue = recordFieldIdDemand.getField(  ).getTitle(  );
                }
            }
        }

        return strRecordFieldValue;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getSMSPhoneNumber(int, int, int)
     */
    @Override
    public String getSMSPhoneNumber( int nPositionPhoneNumber, int nIdRecord, int nIdDirectory )
    {
        String strSMSPhoneNumber = StringUtils.EMPTY;

        strSMSPhoneNumber = getRecordFieldValue( nPositionPhoneNumber, nIdRecord, nIdDirectory );

        return strSMSPhoneNumber;
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getUserGuid(int, int, int)
     */
    @Override
    public String getUserGuid( int nPositionUserGuid, int nIdRecord, int nIdDirectory )
    {
        String strUserGuid = StringUtils.EMPTY;

        if ( nPositionUserGuid != DirectoryUtils.CONSTANT_ID_NULL )
        {
            strUserGuid = getRecordFieldValue( nPositionUserGuid, nIdRecord, nIdDirectory );
        }

        return strUserGuid;
    }

    /**
     * Fill model with user attributes.
     *
     * @param model the model
     * @param strUserGuid the str user guid
     */
    private void fillModelWithUserAttributes( Map<String, Object> model, String strUserGuid )
    {
        if ( _userAttributesManager.isEnabled(  ) && StringUtils.isNotBlank( strUserGuid ) )
        {
            Map<String, String> mapUserAttributes = _userAttributesManager.getAttributes( strUserGuid );
            String strFirstName = mapUserAttributes.get( LuteceUser.NAME_GIVEN );
            String strLastName = mapUserAttributes.get( LuteceUser.NAME_FAMILY );
            String strEmail = mapUserAttributes.get( LuteceUser.BUSINESS_INFO_ONLINE_EMAIL );
            String strPhoneNumber = mapUserAttributes.get( LuteceUser.BUSINESS_INFO_TELECOM_TELEPHONE_NUMBER );

            model.put( NotifyGruDirectoryConstants.MARK_FIRST_NAME,
                StringUtils.isNotEmpty( strFirstName ) ? strFirstName : StringUtils.EMPTY );
            model.put( NotifyGruDirectoryConstants.MARK_LAST_NAME,
                StringUtils.isNotEmpty( strLastName ) ? strLastName : StringUtils.EMPTY );
            model.put( NotifyGruDirectoryConstants.MARK_EMAIL,
                StringUtils.isNotEmpty( strEmail ) ? strEmail : StringUtils.EMPTY );
            model.put( NotifyGruDirectoryConstants.MARK_PHONE_NUMBER,
                StringUtils.isNotEmpty( strPhoneNumber ) ? strPhoneNumber : StringUtils.EMPTY );
        }
    }

    /* (non-Javadoc)
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getLocale(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public Locale getLocale( HttpServletRequest request )
    {
        Locale locale = null;

        if ( request != null )
        {
            locale = request.getLocale(  );
        }
        else
        {
            locale = I18nService.getDefaultLocale(  );
        }

        return locale;
    }

    /**
     * Gets the base url.
     *
     * @param request the request
     * @return the base url
     */
    private String getBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl = StringUtils.EMPTY;

        if ( request != null )
        {
            strBaseUrl = AppPathService.getBaseUrl( request );
        }
        else
        {
            strBaseUrl = AppPropertiesService.getProperty( NotifyGruDirectoryConstants.PROPERTY_LUTECE_ADMIN_PROD_URL );

            if ( StringUtils.isBlank( strBaseUrl ) )
            {
                strBaseUrl = AppPropertiesService.getProperty( NotifyGruDirectoryConstants.PROPERTY_LUTECE_BASE_URL );

                if ( StringUtils.isBlank( strBaseUrl ) )
                {
                    strBaseUrl = AppPropertiesService.getProperty( NotifyGruDirectoryConstants.PROPERTY_LUTECE_PROD_URL );
                }
            }
        }

        return strBaseUrl;
    }
}
