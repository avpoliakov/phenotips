/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.phenotips.distribution.internal;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.annotation.InstantiationStrategy;
import org.xwiki.component.descriptor.ComponentInstantiationStrategy;
import org.xwiki.context.Execution;
import org.xwiki.extension.distribution.internal.job.step.AbstractDistributionStep;
import org.xwiki.stability.Unstable;
import org.xwiki.store.legacy.store.internal.FilesystemAttachmentStore;

import javax.inject.Inject;
import javax.inject.Named;

import com.xpn.xwiki.XWiki;
import com.xpn.xwiki.XWikiContext;

@Unstable
@Component
@Named(AttachmentMigrationDistributionStep.ID)
@InstantiationStrategy(ComponentInstantiationStrategy.PER_LOOKUP)
public class AttachmentMigrationDistributionStep extends AbstractDistributionStep
{
    public static final String ID = "storage.attachmentFilesystemMigrator";

    @Inject
    private FilesystemAttachmentStore store;

    @Inject
    private Execution execution;

    public AttachmentMigrationDistributionStep()
    {
        super(ID);
    }

    @Override
    public void prepare()
    {
        XWikiContext context = (XWikiContext) this.execution.getContext().getProperty("xwikicontext");
        XWiki xwiki = context.getWiki();
        if (!isFilesystemStore(xwiki.getAttachmentStore())) {
            setState(State.COMPLETED);
            return;
        }
        // TODO Auto-generated method stub
    }

    private boolean isFilesystemStore(final Object store)
    {
        return store.getClass().getName().contains("Filesystem");
    }

}
