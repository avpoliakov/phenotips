package org.phenotips.storage.migrators.internal;

import org.phenotips.storage.migrators.DataReader;

import org.xwiki.model.reference.EntityReference;

import java.util.Iterator;

import javax.inject.Inject;

import org.hibernate.Criteria;
import org.hibernate.Session;

import com.xpn.xwiki.doc.XWikiAttachment;
import com.xpn.xwiki.store.hibernate.HibernateSessionFactory;

public class FilesystemAttachmentsReader implements DataReader<XWikiAttachment>
{
    @Inject
    private HibernateSessionFactory hibernate;

    @Override
    public boolean hasData()
    {
        Session session = null;
        try {
            session = this.hibernate.getSessionFactory().openSession();
            Criteria c = session.createCriteria(XWikiAttachment.class);
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
            Criteria c = session.createCriteria(XWikiAttachment.class);
            c.setMaxResults(1);
            return !c.list().isEmpty();
        } finally {
            session.close();
        }
        return null;
    }

    @Override
    public Iterator<XWikiAttachment> getData()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean discardItem(XWikiAttachment item)
    {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean discardAllData()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
