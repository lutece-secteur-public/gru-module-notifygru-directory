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

import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IMarkerProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.NotifyGruMarker;
import fr.paris.lutece.plugins.workflow.service.taskinfo.AbstractTaskInfoProvider;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.plugins.workflowcore.service.task.ITaskService;
import javax.inject.Named;
import net.sf.json.JSONObject;

/**
 * This class represents a NotifyGru marker provider for the Edit record task
 *
 */
public class ReplyMarkerProvider implements IMarkerProvider
{
    private static final String ID = "workflow-reply.taskReplyMarkerProvider";

    // Messages
    private static final String MESSAGE_TITLE_KEY = "module.notifygru.directory.marker.provider.taskreply.title";
    private static final String MESSAGE_MARKER_MSG_DESCRIPTION_KEY = "module.notifygru.directory.marker.provider.taskreply.msg.description";

    // Markers
    private static final String MARK_REPLY_MESSAGE = "task_reply_message";
    

    // Services
    @Inject
    private ITaskService _taskService;

    @Inject
    @Named(value="workflow-reply.replyTaskInfoProvider")
    private AbstractTaskInfoProvider _replyTaskInfoProvider;

    
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
        return MESSAGE_TITLE_KEY ;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<NotifyGruMarker> provideMarkerDescriptions( )
    {
        List<NotifyGruMarker> listMarkers = new ArrayList<>( );

        NotifyGruMarker notifyGruMarkerMsg = new NotifyGruMarker( MARK_REPLY_MESSAGE );
        notifyGruMarkerMsg.setDescription( MESSAGE_MARKER_MSG_DESCRIPTION_KEY );
        listMarkers.add( notifyGruMarkerMsg );

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
            if ( taskOther.getTaskType( ).getKey( ).equals( _replyTaskInfoProvider.getTaskType( ).getKey( ) ) )
            {
                String strJsonInfos = _replyTaskInfoProvider.getTaskResourceInfo( resourceHistory.getId( ), taskOther.getId( ), request ) ;

                JSONObject jsonInfos = JSONObject.fromObject( strJsonInfos );
                String strMsg = jsonInfos.getString( MARK_REPLY_MESSAGE );


                NotifyGruMarker notifyGruMarkerMsg = new NotifyGruMarker( MARK_REPLY_MESSAGE );
                notifyGruMarkerMsg.setValue( strMsg );
                listMarkers.add( notifyGruMarkerMsg );

                break;
            }
        }

        return listMarkers;
    }

}

