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
package fr.paris.lutece.plugins.notifygru.modules.directory.services;

import fr.paris.lutece.plugins.directory.business.EntryFilter;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.Field;
import fr.paris.lutece.plugins.directory.business.FieldHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.service.NotifygruMappingManagerService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * NotifyDirectoryService.
 */
public final class NotifyGruDirectoryService implements INotifyGruDirectoryService
{
    /** The Constant BEAN_SERVICE. */
    public static final String BEAN_SERVICE = "notifygru-directory.ProviderDirectoryService";

    // PROPERTIES
    private static final String PROPERTY_ENTRY_TYPE_NUMBERING = "directory.entry_type.numbering";
    private static final String PROPERTY_REFUSED_DIRECTORY_ENTRY_TYPES = "workflow-notifygrudirectory.refusedDirectoryEntryTypes";

    // Other constants
    private static final String COMMA = ",";

    /** The _list refused entry types. */
    private final List<Integer> _listRefusedEntryTypes;

    /**
     * Instantiates a new notify gru directory service.
     */
    private NotifyGruDirectoryService( )
    {
        // Init list refused entry types
        _listRefusedEntryTypes = fillListEntryTypes( PROPERTY_REFUSED_DIRECTORY_ENTRY_TYPES );
    }

    /**
     * Fill list entry types.
     *
     * @param strPropertyEntryTypes
     *            the str property entry types
     * @return the list
     */
    private static List<Integer> fillListEntryTypes( String strPropertyEntryTypes )
    {
        List<Integer> listEntryTypes = new ArrayList<Integer>( );
        String strEntryTypes = AppPropertiesService.getProperty( strPropertyEntryTypes );

        if ( StringUtils.isNotBlank( strEntryTypes ) )
        {
            String [ ] listAcceptEntryTypesForIdDemand = strEntryTypes.split( COMMA );

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

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#isEntryTypeRefused(int)
     */
    @Override
    public boolean isEntryTypeRefused( int nIdEntryType )
    {
        boolean bIsRefused = true;

        if ( ( _listRefusedEntryTypes != null ) && !_listRefusedEntryTypes.isEmpty( ) )
        {
            bIsRefused = _listRefusedEntryTypes.contains( nIdEntryType );
        }

        return bIsRefused;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getListEntries(int)
     */
    @Override
    public List<IEntry> getEntries( int nidDirectory )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        List<IEntry> listEntries = new ArrayList<IEntry>( );
        EntryFilter entryFilter = new EntryFilter( );
        entryFilter.setIdDirectory( nidDirectory );

        List<IEntry> listAllEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

        for ( IEntry entry : listAllEntries )
        {
            if ( entry.getEntryType( ).getGroup( ) )
            {
                if ( entry.getChildren( ) != null )
                {
                    for ( IEntry child : entry.getChildren( ) )
                    {
                        if ( !isEntryTypeRefused( child.getEntryType( ).getIdType( ) ) )
                        {
                            listEntries.add( child );
                        }
                    }
                }
            }
            else
            {
                if ( !isEntryTypeRefused( entry.getEntryType( ).getIdType( ) ) )
                {
                    listEntries.add( entry );
                }
            }
        }

        return listEntries;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getEmail(int, int, int)
     */
    @Override
    public String getEmail( int nPositionEmail, int nIdRecord, int nIdDirectory )
    {
        String strEmail = StringUtils.EMPTY;

        strEmail = getRecordFieldValue( nPositionEmail, nIdRecord, nIdDirectory );

        return strEmail;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getIdDemand(int, int, int)
     */
    @Override
    public int getIdDemand( int nPositionDemand, int nIdRecord, int nIdDirectory )
    {
        String strIdDemand = StringUtils.EMPTY;

        strIdDemand = getRecordFieldValue( nPositionDemand, nIdRecord, nIdDirectory );

        return NumberUtils.toInt( strIdDemand, -1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getIdDemandType(int, int, int)
     */
    @Override
    public int getIdDemandType( int nPositionDemandType, int nIdRecord, int nIdDirectory )
    {
        String strIdDemandType = StringUtils.EMPTY;

        strIdDemandType = getRecordFieldValue( nPositionDemandType, nIdRecord, nIdDirectory );

        return NumberUtils.toInt( strIdDemandType, -1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getRecordFieldValue(int, int, int)
     */
    @Override
    public String getRecordFieldValue( int nPosition, int nIdRecord, int nIdDirectory )
    {
        String strRecordFieldValue = StringUtils.EMPTY;
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );

        if ( nPosition != NotifygruMappingManagerService.MAPPING_POSITION_NONE )
        {
            // RecordField
            EntryFilter entryFilter = new EntryFilter( );
            entryFilter.setPosition( nPosition );
            entryFilter.setIdDirectory( nIdDirectory );

            List<IEntry> listEntries = EntryHome.getEntryList( entryFilter, pluginDirectory );

            if ( ( listEntries != null ) && !listEntries.isEmpty( ) )
            {
                IEntry entry = listEntries.get( 0 );
                RecordFieldFilter recordFieldFilterEmail = new RecordFieldFilter( );
                recordFieldFilterEmail.setIdDirectory( nIdDirectory );
                recordFieldFilterEmail.setIdEntry( entry.getIdEntry( ) );
                recordFieldFilterEmail.setIdRecord( nIdRecord );

                List<RecordField> listRecordFields = RecordFieldHome.getRecordFieldList( recordFieldFilterEmail, pluginDirectory );

                if ( ( listRecordFields != null ) && !listRecordFields.isEmpty( ) && ( listRecordFields.get( 0 ) != null ) )
                {
                    RecordField recordFieldIdDemand = listRecordFields.get( 0 );
                    strRecordFieldValue = recordFieldIdDemand.getValue( );
                    
                    if ( isDirectoryNumberingType( entry.getEntryType().getIdType() ) && recordFieldIdDemand.getEntry() != null )
                    {
                       List<Field> listFieldsIdDemand = FieldHome.getFieldListByIdEntry( recordFieldIdDemand.getEntry().getIdEntry(), pluginDirectory );
                        if ( !listFieldsIdDemand.isEmpty( ) )
                        {
                            strRecordFieldValue = listFieldsIdDemand.get( 0 ).getTitle( ) + strRecordFieldValue;
                        } 
                    }
                }
            }
        }

        return strRecordFieldValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.paris.lutece.plugins.notifygru.modules.directory.services.INotifyGruDirectoryService#getSMSPhoneNumber(int, int, int)
     */
    @Override
    public String getSMSPhoneNumber( int nPositionPhoneNumber, int nIdRecord, int nIdDirectory )
    {
        String strSMSPhoneNumber = StringUtils.EMPTY;

        strSMSPhoneNumber = getRecordFieldValue( nPositionPhoneNumber, nIdRecord, nIdDirectory );

        return strSMSPhoneNumber;
    }

    /*
     * (non-Javadoc)
     * 
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
     * Check if the given id entry type is an entry type numbering
     * 
     * @param nIdEntryType
     *            the id entry type
     * @return true if it is an entry type numbering, false otherwise
     */
    public static boolean isDirectoryNumberingType( int nIdEntryType )
    {
        String strMappingNumberingType = AppPropertiesService.getProperty( PROPERTY_ENTRY_TYPE_NUMBERING );

        if ( strMappingNumberingType != null )
        {
            String [ ] tabNumericType = strMappingNumberingType.split( "," );

            for ( String strIdTypeNumeric : tabNumericType )
            {
                if ( nIdEntryType == DirectoryUtils.convertStringToInt( strIdTypeNumeric ) )
                {
                    return true;
                }
            }
        }

        return false;
    }
}
