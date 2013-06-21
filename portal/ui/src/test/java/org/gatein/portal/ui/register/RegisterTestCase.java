/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.gatein.portal.ui.register;

import java.io.File;
import java.net.URL;

import juzu.arquillian.Helper;
import org.gatein.portal.common.kernel.KernelLifeCycle;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.descriptor.api.Descriptors;
import org.jboss.shrinkwrap.descriptor.api.portletapp20.PortletDescriptor;
import org.jboss.shrinkwrap.descriptor.api.webapp30.WebAppDescriptor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import static org.junit.Assert.*;

/**
 * @author Julien Viet
 */
@RunWith(Arquillian.class)
public class RegisterTestCase {

    @Deployment
    public static WebArchive getDeployment() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "portal.war");
        Helper.createBasePortletDeployment(war, "guice", Controller.class);
        PortletDescriptor descriptor = Descriptors.
                create(PortletDescriptor.class).
                createPortlet().
                portletName("RegisterPortlet").
                portletClass(RegisterPortlet.class.getName()).
                up();
        war.addAsWebInfResource(new StringAsset(descriptor.exportAsString()), "portlet.xml");
        Node node = war.get("WEB-INF/web.xml");
        WebAppDescriptor webApp = Descriptors.importAs(WebAppDescriptor.class).fromStream(node.getAsset().openStream());
        webApp.displayName("portal").createFilter().filterName("KernelLifeCycle").filterClass(KernelLifeCycle.class.getName()).up().
                createFilterMapping().filterName("KernelLifeCycle").servletName("EmbedServlet").up();
        war.delete(node.getPath());
        war.setWebXML(new StringAsset(webApp.exportAsString()));
        return war;
    }

    @Drone
    WebDriver driver;

    @Test
    @RunAsClient
    public void testFoo(@ArquillianResource URL deploymentURL) throws Exception {
        URL url = deploymentURL.toURI().resolve("./embed/RegisterPortlet").toURL();
        driver.get(url.toString());

        // Really dumb test for now
        WebElement register = driver.findElement(By.cssSelector("div.register-unit"));
        assertNotNull(register);
    }
}