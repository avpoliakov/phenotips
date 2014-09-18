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

import org.phenotips.storage.migrators.DataReader;
import org.phenotips.storage.migrators.DataTypeMigrator;
import org.phenotips.storage.migrators.DataWriter;

import org.xwiki.component.manager.ComponentLookupException;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.component.util.DefaultParameterizedType;
import org.xwiki.configuration.ConfigurationSource;

import java.lang.reflect.ParameterizedType;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.slf4j.Logger;

/**
 * @version $Id$
 */
public abstract class AbstractDataTypeMigrator<T> implements DataTypeMigrator<T>
{
    private static final String DEFAULT_STORE = "hibernate";

    @Inject
    private Logger logger;

    @Inject
    @Named("legacy")
    private ConfigurationSource config;

    @Inject
    protected Provider<ComponentManager> cm;

    @Override
    public boolean migrate()
    {
        DataWriter<T> writer = getWriter();
        Map<String, DataReader<T>> readers;
        try {
            readers = this.cm.get().getInstanceMap(
                new DefaultParameterizedType(null, DataReader.class, ((ParameterizedType) this.getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0]));
        } catch (ComponentLookupException e) {
            return false;
        }
        for (Map.Entry<String, DataReader<T>> entry : readers.entrySet()) {
            DataReader<T> reader = entry.getValue();
            if (!reader.hasData()) {
                continue;
            }

            if (writer == null) {
                // No writer found, keep data in place
                this.logger.warn(
                    "Legacy attachments found in [{}], but no current storage configured; keeping legacy data",
                    entry.getKey());
                return false;
            }

            if (reader.getType().equals(writer.getType())) {
                continue;
            }

            Iterator<T> data = reader.getData();
            while (data.hasNext()) {
                T item = data.next();
                if (writer.storeItem(item)) {
                    reader.discardItem(item);
                }
            }
        }
        return true;
    }

    private DataWriter<T> getWriter()
    {
        Object cfg = this.config.getProperty(getStoreConfigurationKey());
        String hint = DEFAULT_STORE;
        if (cfg instanceof List) {
            @SuppressWarnings("unchecked")
            List<String> cfgValues = (List<String>) cfg;
            hint = cfgValues.get(cfgValues.size() - 1);
        } else if (cfg instanceof String) {
            hint = (String) cfg;
        }
        try {
            return this.cm.get().getInstance(
                new DefaultParameterizedType(null, DataWriter.class, ((ParameterizedType) this.getClass()
                    .getGenericSuperclass()).getActualTypeArguments()[0]),
                getDataType() + "/" + hint);
        } catch (ComponentLookupException e) {
            return null;
        }
    }

    protected abstract String getStoreConfigurationKey();
}
