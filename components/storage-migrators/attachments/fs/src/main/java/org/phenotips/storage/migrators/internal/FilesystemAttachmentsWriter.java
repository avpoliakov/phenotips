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
package org.phenotips.storage.migrators.internal;

import org.phenotips.storage.migrators.DataWriter;
import org.phenotips.storage.migrators.Type;

import org.xwiki.component.annotation.Component;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiAttachmentContent;
import com.xpn.xwiki.store.XWikiAttachmentStoreInterface;

@Component
@Named("attachments/file")
@Singleton
public class FilesystemAttachmentsWriter implements DataWriter<XWikiAttachmentContent>
{
    public static final Type TYPE = new Type("attachments", "file");

    @Inject
    @Named("file")
    private XWikiAttachmentStoreInterface store;

    @Inject
    private Provider<XWikiContext> context;

    @Override
    public Type getType()
    {
        return TYPE;
    }

    @Override
    public boolean storeItem(XWikiAttachmentContent item)
    {
        XWikiAttachment existing =
            new XWikiAttachment(item.getAttachment().getDoc(), item.getAttachment().getFilename());
        try {
            this.store.loadAttachmentContent(existing, this.context.get(), false);
            // If loading succeeded, then the attachment already exists on the filesystem;
            // keep using the existing attachment version and discard the database one
            return true;
        } catch (XWikiException e) {
            // No such attachment on the filesystem, continue storing it
        }
        try {
            this.store.saveAttachmentContent(item.getAttachment(), false, this.context.get(), false);
            return true;
        } catch (XWikiException e) {
        }
        return false;
    }
}
