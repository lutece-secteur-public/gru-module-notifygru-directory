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

import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.util.ReferenceList;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;


/**
 * The Interface INotifyGruDirectoryService.
 */
public interface INotifyGruDirectoryService
{
    /**
     * Checks if is entry type email sms accepted.
     *
     * @param nIdEntryType the n id entry type
     * @return true, if is entry type email sms accepted
     */
    boolean isEntryTypeEmailSMSAccepted( int nIdEntryType );

    /**
     * Checks if is entry type user guid accepted.
     *
     * @param nIdEntryType the n id entry type
     * @return true, if is entry type user guid accepted
     */
    boolean isEntryTypeUserGuidAccepted( int nIdEntryType );

    /**
     * Checks if is entry type file accepted.
     *
     * @param nIdEntryType the n id entry type
     * @return true, if is entry type file accepted
     */
    boolean isEntryTypeFileAccepted( int nIdEntryType );

    /**
     * Checks if is entry type refused.
     *
     * @param nIdEntryType the n id entry type
     * @return true, if is entry type refused
     */
    boolean isEntryTypeRefused( int nIdEntryType );

    /**
     * Gets the list states.
     *
     * @param nIdAction the n id action
     * @return the list states
     */
    ReferenceList getListStates( int nIdAction );

    /**
     * Gets the list directories.
     *
     * @return the list directories
     */
    ReferenceList getListDirectories(  );

    /**
     * Gets the mailing list.
     *
     * @param request the request
     * @return the mailing list
     */
    ReferenceList getMailingList( HttpServletRequest request );

    /**
     * Gets the list entries user guid.
     *
     * @param nidDirectory the nid directory
     * @param locale the locale
     * @return the list entries user guid
     */
    ReferenceList getListEntriesUserGuid( int nidDirectory, Locale locale );

    /**
     * Gets the list entries email sms.
     *
     * @param nidDirectory the nid directory
     * @param locale the locale
     * @return the list entries email sms
     */
    ReferenceList getListEntriesEmailSMS( int nidDirectory, Locale locale );

    /**
     * Gets the list entries freemarker.
     *
     * @param nidDirectory the nid directory
     * @return the list entries freemarker
     */
    List<IEntry> getListEntriesFreemarker( int nidDirectory );

    /**
     * Gets the list entries file.
     *
     * @param nidDirectory the nid directory
     * @param locale the locale
     * @return the list entries file
     */
    List<IEntry> getListEntriesFile( int nidDirectory, Locale locale );

    /**
     * Gets the list entries.
     *
     * @param nidDirectory the nid directory
     * @return the list entries
     */
    List<IEntry> getListEntries( int nidDirectory );

    /**
     * Gets the email.
     *
     * @param nPositionEmail the n position email
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the email
     */
    String getEmail( int nPositionEmail, int nIdRecord, int nIdDirectory );

    /**
     * Gets the id demand.
     *
     * @param nPositionDemand the n position demand
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the id demand
     */
    int getIdDemand( int nPositionDemand, int nIdRecord, int nIdDirectory );

    /**
     * Gets the id demand type.
     *
     * @param nPositionDemandType the n position demand type
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the id demand type
     */
    int getIdDemandType( int nPositionDemandType, int nIdRecord, int nIdDirectory );

    /**
     * Gets the SMS phone number.
     *
     * @param nPositionPhoneNumber the n position phone number
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the SMS phone number
     */
    String getSMSPhoneNumber( int nPositionPhoneNumber, int nIdRecord, int nIdDirectory );

    /**
     * Gets the user guid.
     *
     * @param nPositionUserGuid the n position user guid
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the user guid
     */
    String getUserGuid( int nPositionUserGuid, int nIdRecord, int nIdDirectory );

    /**
     * Gets the record field value.
     *
     * @param nPosition the n position
     * @param nIdRecord the n id record
     * @param nIdDirectory the n id directory
     * @return the record field value
     */
    String getRecordFieldValue( int nPosition, int nIdRecord, int nIdDirectory );

    /**
     * Gets the locale.
     *
     * @param request the request
     * @return the locale
     */
    Locale getLocale( HttpServletRequest request );
}
