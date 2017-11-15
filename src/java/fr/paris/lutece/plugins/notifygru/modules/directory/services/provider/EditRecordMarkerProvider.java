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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.workflow.modules.editrecord.service.taskinfo.EditRecordTaskInfoProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;

/**
 * This class represents a NotifyGru marker provider for the Edit record task
 *
 */
public class EditRecordMarkerProvider implements IMarkerProvider
{
    private static final String ID = "notifygru-directory.editRecordMarkerProvider";

    // Messages
    private static final String MESSAGE_TITLE = "module.notifygru.directory.marker.provider.editrecord.title";
    private static final String MESSAGE_MARKER_URL_DESCRIPTION = "module.notifygru.directory.marker.provider.editrecord.url.description";

    // Markers
    private static final String MARK_EDIT_RECORD_URL = "edit_record_url";

    // Services
    @Inject
    private ITaskService _taskService;

    @Inject
    private EditRecordTaskInfoProvider _editRecordTaskInfoProvider;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getId( )
    {
        return ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitleI18nKey( )
    {
        return MESSAGE_TITLE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotifyGruMarker> provideMarkerDescriptions( )
    {
        List<NotifyGruMarker> listMarkers = new ArrayList<>( );

        NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_EDIT_RECORD_URL );
        notifyGruMarker.setDescription( MESSAGE_MARKER_URL_DESCRIPTION );
        listMarkers.add( notifyGruMarker );

        return listMarkers;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotifyGruMarker> provideMarkerValues( ResourceHistory resourceHistory, ITask task, HttpServletRequest request )
    {
        List<NotifyGruMarker> listMarkers = new ArrayList<>( );

        for ( ITask taskOther : _taskService.getListTaskByIdAction( resourceHistory.getAction( ).getId( ), request.getLocale( ) ) )
        {
            if ( taskOther.getTaskType( ).getKey( ).equals( _editRecordTaskInfoProvider.getTaskType( ).getKey( ) ) )
            {
                NotifyGruMarker notifyGruMarker = new NotifyGruMarker( MARK_EDIT_RECORD_URL );
                notifyGruMarker.setValue( _editRecordTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request ) );
                listMarkers.add( notifyGruMarker );

                break;
            }
        }

        return listMarkers;
    }

}
