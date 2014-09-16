package org.phenotips.storage.migrators.internal;

import org.phenotips.storage.migrators.DataReader;
import org.phenotips.storage.migrators.Type;

import org.xwiki.component.annotation.Component;
import org.xwiki.model.reference.AttachmentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReference;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.xpn.xwiki.doc.XWikiAttachmentContent;
import com.xpn.xwiki.store.XWikiAttachmentStoreInterface;
import com.xpn.xwiki.store.hibernate.HibernateSessionFactory;

@Component
@Named("attachments/hibernate")
@Singleton
public class HibernateAttachmentsReader implements DataReader<XWikiAttachmentContent>
{
    @Inject
    private HibernateSessionFactory hibernate;

    @Inject
    @Named("hibernate")
    private XWikiAttachmentStoreInterface store;

    @Inject
    @Named("current")
    private DocumentReferenceResolver<String> resolver;

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
        return false;
    }

    @Override
    public boolean discardAllData()
    {
        // TODO Auto-generated method stub
        return false;
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
            return new AttachmentReference(item[1], HibernateAttachmentsReader.this.resolver.resolve(item[0]));
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
            return null;
        }

        @Override
        public void remove()
        {
            // TODO Auto-generated method stub
        }
    }
}
