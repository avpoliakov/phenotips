package org.phenotips.storage.migrators.internal;

import org.phenotips.storage.migrators.DataWriter;
import org.phenotips.storage.migrators.Type;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.doc.XWikiAttachmentContent;
import com.xpn.xwiki.doc.XWikiDocument;

@Component
@Named("attachments/hibernate")
@Singleton
public class FilesystemAttachmentsWriter implements DataWriter<XWikiAttachmentContent>
{
    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> resolver;

    @Inject
    private DocumentAccessBridge dab;

    @Inject
    private Provider<XWikiContext> context;

    @Override
    public Type getType()
    {
        return AttachmentsMigrator.TYPE;
    }

    @Override
    public boolean hasData()
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            Criteria c = session.createCriteria(XWikiAttachmentContent.class);
            c.setMaxResults(1);
            return !c.list().isEmpty();
        } finally {
            session.close();
        }
    }

    @Override
    public Iterator<EntityReference> listData()
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            @SuppressWarnings("unchecked")
            Iterator<String[]> data = session.createQuery(
                "select d.fullName, a.filename from XWikiDocument d, XWikiAttachment a, XWikiAttachmentContent c"
                    + " where a.docId = d.id and c.id = a.id").iterate();
            return new ReferenceIterator(data);
        } finally {
            session.close();
        }
    }

    @Override
    public Iterator<XWikiAttachmentContent> getData()
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            @SuppressWarnings("unchecked")
            Iterator<String[]> data = session.createQuery(
                "select d.fullName, a.filename from XWikiDocument d, XWikiAttachment a, XWikiAttachmentContent c"
                    + " where a.docId = d.id and c.id = a.id").iterate();
            return new AttachmentIterator(data);
        } finally {
            session.close();
        }
    }

    @Override
    public boolean discardItem(XWikiAttachmentContent item)
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            Transaction t = session.beginTransaction();
            session.delete(item);
            t.commit();
            return true;
        } finally {
            session.close();
        }
    }

    @Override
    public boolean discardAllData()
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            Transaction t = session.beginTransaction();
            session.createQuery("delete from XWikiAttachmentContent").executeUpdate();
            t.commit();
            return true;
        } finally {
            session.close();
        }
    }

    private class ReferenceIterator implements Iterator<EntityReference>
    {
        private Iterator<String[]> data;

        ReferenceIterator(Iterator<String[]> data)
        {
            this.data = data;
        }

        @Override
        public boolean hasNext()
        {
            return this.data.hasNext();
        }

        @Override
        public EntityReference next()
        {
            String[] item = this.data.next();
            return new AttachmentReference(item[1], FilesystemAttachmentsWriter.this.resolver.resolve(item[0]));
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }

    private class AttachmentIterator implements Iterator<XWikiAttachmentContent>
    {
        private Iterator<String[]> data;

        AttachmentIterator(Iterator<String[]> data)
        {
            this.data = data;
        }

        @Override
        public boolean hasNext()
        {
            return this.data.hasNext();
        }

        @Override
        public XWikiAttachmentContent next()
        {
            String[] item = this.data.next();
            try {
                XWikiDocument doc = (XWikiDocument) FilesystemAttachmentsWriter.this.dab.getDocument(
                    FilesystemAttachmentsWriter.this.resolver.resolve(item[0]));
                XWikiAttachment att = new XWikiAttachment(doc, item[1]);
                FilesystemAttachmentsWriter.this.store.loadAttachmentContent(att,
                    FilesystemAttachmentsWriter.this.context.get(), false);
                return att.getAttachment_content();
            } catch (Exception e) {

            }
            return null;
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
