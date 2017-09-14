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

import fr.paris.lutece.plugins.directory.business.Directory;
import fr.paris.lutece.plugins.directory.business.DirectoryFilter;
import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.notifygru.modules.directory.services.NotifyGruDirectoryConstants;
import fr.paris.lutece.plugins.modulenotifygrumappingmanager.service.AbstractProviderManagerWithMapping;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.IProvider;
import fr.paris.lutece.plugins.workflow.modules.notifygru.service.provider.ProviderDescription;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.business.workflow.Workflow;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.util.ReferenceList;

/**
 * <p>
 * This class represents a provider manager for {@link Directory} objects
 * </p>
 * <p>
 * One provider per {@code Directory} object
 * </p>
 *
 */
public class DirectoryProviderManager extends AbstractProviderManagerWithMapping
{
    @Inject
    private ActionService _actionService;

    /**
     * Constructor
     * 
     * @param strId
     *            the id of this provider manager
     */
    public DirectoryProviderManager( String strId )
    {
        super( strId );
    }

    /**
     * {@inheritDoc}
     * <p>
     * Only the providers for directories linked to the current workflow are returned
     * </p>
     */
    @Override
    public Collection<ProviderDescription> getAllProviderDescriptions( ITask task )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );
        DirectoryFilter filter = new DirectoryFilter( );
        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( Directory directory : listDirectory )
        {
            Action action = _actionService.findByPrimaryKey( task.getAction( ).getId( ) );
            Workflow wf = action.getWorkflow( );

            if ( ( wf.getId( ) == directory.getIdWorkflow( ) ) )
            {
                ProviderDescription providerDescription = new ProviderDescription( String.valueOf( directory.getIdDirectory( ) ),
                        I18nService.getLocalizedString( NotifyGruDirectoryConstants.TITLE_I18NKEY, I18nService.getDefaultLocale( ) ) + directory.getTitle( ) );
                collectionProviderDescriptions.add( providerDescription );
            }
        }

        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ProviderDescription getProviderDescription( String strProviderId )
    {
        Directory directory = DirectoryHome.findByPrimaryKey( Integer.parseInt( strProviderId ), PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        ProviderDescription providerDescription = new ProviderDescription( String.valueOf( directory.getIdDirectory( ) ), I18nService.getLocalizedString(
                NotifyGruDirectoryConstants.TITLE_I18NKEY, I18nService.getDefaultLocale( ) ) + directory.getTitle( ) );

        providerDescription.setMarkerDescriptions( DirectoryProvider.getProviderMarkerDescriptions( strProviderId ) );

        return providerDescription;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IProvider createProvider( String strProviderId, ResourceHistory resourceHistory, HttpServletRequest request )
    {
        return new DirectoryProvider( getId( ), strProviderId, resourceHistory );
    }

    /**
     * <p>
     * Gives the provider description for all the directories.
     * </p>
     * <p>
     * Differ from {@link #getAllProviderDescriptions(ITask )} : this method returns the provider description for all the directories, not only the ones linked
     * to the current workflow.
     * 
     * @return all the provider descriptions
     */
    public Collection<ProviderDescription> getAllProviderDescriptions( )
    {
        Collection<ProviderDescription> collectionProviderDescriptions = new ArrayList<>( );
        DirectoryFilter filter = new DirectoryFilter( );
        List<Directory> listDirectory = DirectoryHome.getDirectoryList( filter, PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME ) );

        for ( Directory directory : listDirectory )
        {
            ProviderDescription providerDescription = new ProviderDescription( String.valueOf( directory.getIdDirectory( ) ), I18nService.getLocalizedString(
                    NotifyGruDirectoryConstants.TITLE_I18NKEY, I18nService.getDefaultLocale( ) ) + directory.getTitle( ) );
            collectionProviderDescriptions.add( providerDescription );
        }

        return collectionProviderDescriptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ReferenceList getMappingPropertiesForProvider( String strProviderId )
    {
        return DirectoryProvider.getEntryPositions( strProviderId );
    }

}
