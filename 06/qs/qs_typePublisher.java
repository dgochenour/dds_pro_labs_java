//*****************************************************************************
//*    (c) 2005-2018 Copyright, Real-Time Innovations, All rights reserved.   *
//*                                                                           *
//*         Permission to modify and use for internal purposes granted.       *
//* This software is provided "as is", without warranty, express or implied.  *
//*                                                                           *
//*****************************************************************************

package qs;

/* qs_typePublisher.java

A publication of data of type qs_type

This file is derived from code automatically generated by the rtiddsgen 
command:

rtiddsgen -language java -example <arch> .idl

Example publication of type qs_type automatically generated by 
'rtiddsgen' To test them follow these steps:

(1) Compile this file and the example subscription.

(2) Start the subscription on the same domain used for RTI Data Distribution
Service with the command
java qs_typeSubscriber <domain_id> <sample_count>

(3) Start the publication on the same domain used for RTI Data Distribution
Service with the command
java qs_typePublisher <domain_id> <sample_count>

(4) [Optional] Specify the list of discovery initial peers and 
multicast receive addresses via an environment variable or a file 
(in the current working directory) called NDDS_DISCOVERY_PEERS.  

You can run any number of publishers and subscribers programs, and can 
add and remove them dynamically from the domain.

Example:

To run the example application on domain <domain_id>:

Ensure that $(NDDSHOME)/lib/<arch> is on the dynamic library path for
Java.                       

On Unix: 
add $(NDDSHOME)/lib/<arch> to the 'LD_LIBRARY_PATH' environment
variable

On Windows:
add %NDDSHOME%\lib\<arch> to the 'Path' environment variable

Run the Java applications:

java -Djava.ext.dirs=$NDDSHOME/lib/java qs_typePublisher <domain_id>

java -Djava.ext.dirs=$NDDSHOME/lib/java qs_typeSubscriber <domain_id>        
*/

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import com.rti.dds.domain.*;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.*;
import com.rti.dds.topic.*;
import com.rti.ndds.config.*;

// ===========================================================================

public class qs_typePublisher {
    // -----------------------------------------------------------------------
    // Public Methods
    // -----------------------------------------------------------------------

    public static void main(String[] args) {
        // --- Get domain ID --- //
        int domainId = 0;
        if (args.length >= 1) {
            domainId = Integer.valueOf(args[0]).intValue();
        }

        // -- Get max loop count; 0 means infinite loop --- //
        int sampleCount = 0;
        if (args.length >= 2) {
            sampleCount = Integer.valueOf(args[1]).intValue();
        }

        /* Uncomment this to turn on additional logging
        Logger.get_instance().set_verbosity_by_category(
            LogCategory.NDDS_CONFIG_LOG_CATEGORY_API,
            LogVerbosity.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
        */

        // --- Run --- //
        publisherMain(domainId, sampleCount);
    }

    // -----------------------------------------------------------------------
    // Private Methods
    // -----------------------------------------------------------------------

    // --- Constructors: -----------------------------------------------------

    private qs_typePublisher() {
        super();
    }

    // -----------------------------------------------------------------------

    private static void publisherMain(int domainId, int sampleCount) {

        DomainParticipant participant = null;
        Publisher publisher = null;
        Topic topic = null;
        qs_typeDataWriter writer = null;

        try {
            // --- Create participant --- //

            // LAB#6 
            // To load MY_QOS_PROFILES.xml, we need to modify the 
            // DDSTheParticipantFactory Profile QoSPolicy 
        	DomainParticipantFactoryQos factoryQos = 
        		    new DomainParticipantFactoryQos();
        	DomainParticipantFactory.TheParticipantFactory.get_qos(factoryQos);
            
            // LAB#6 
            // We are only adding one XML file to the url_profile sequence, so 
            // we set a maximum length of 1 
        	factoryQos.profile.url_profile.setMaximum(1);

            // LAB#6
            // Now load our custom file into the sequence, and then call
            // set_qos() to make our modified file be the QoS provider
        	factoryQos.profile.url_profile.add("file://MY_QOS_PROFILES.xml");
            DomainParticipantFactory.TheParticipantFactory.set_qos(factoryQos);

            // LAB #6
            // specify profile
            participant = DomainParticipantFactory.TheParticipantFactory.create_participant_with_profile(
                    domainId, 
                    "MyLibrary",
                    "MyProfile",
                    null /* listener */, 
                    StatusKind.STATUS_MASK_NONE);
            if (participant == null) {
                System.err.println("create_participant error\n");
                return;
            }        

            // --- Create publisher --- //

            // LAB #6
            // specify profile
            publisher = participant.create_publisher_with_profile(
                    "MyLibrary",
                    "MyProfile",
                    null /* listener */,
                    StatusKind.STATUS_MASK_NONE);
            if (publisher == null) {
                System.err.println("create_publisher error\n");
                return;
            }                   

            // --- Create topic --- //

            /* Register type before creating topic */
            String typeName = qs_typeTypeSupport.get_type_name();
            qs_typeTypeSupport.register_type(participant, typeName);

            /* To customize topic QoS, use
            the configuration file USER_QOS_PROFILES.xml */

            // LAB #4 - use variable defined in IDL for topic name 
            topic = participant.create_topic(
                qs.topicName.VALUE,                                
                typeName, DomainParticipant.TOPIC_QOS_DEFAULT,
                null /* listener */, StatusKind.STATUS_MASK_NONE);
            if (topic == null) {
                System.err.println("create_topic error\n");
                return;
            }           

            // --- Create writer --- //

            // LAB #6
            // specify profile
            writer = (qs_typeDataWriter)publisher.create_datawriter_with_profile(
                    topic, 
                    "MyLibrary",
                    "MyProfile",
                    null /* listener */, 
                    StatusKind.STATUS_MASK_NONE);
            if (writer == null) {
                System.err.println("create_datawriter error\n");
                return;
            }           

            // --- Write --- //

            /* Create data sample for writing */
            qs_type instance = new qs_type();

            InstanceHandle_t instance_handle = InstanceHandle_t.HANDLE_NIL;
            /* For a data type that has a key, if the same instance is going to be
            written multiple times, initialize the key here
            and register the keyed instance prior to writing */
            //instance_handle = writer.register_instance(instance);

            // LAB #3
            // change writes to 10Hz
            final long sendPeriodMillis = 100; 

            for (int count = 0;
            (sampleCount == 0) || (count < sampleCount);
            ++count) {
                System.out.println("Writing qs_type, count " + count);

                /* Modify the instance to be written here */

                // LAB #1
                instance.name = "Don Gochenour";
                instance.id = 9017;
                instance.value1 = count;

                /* Write data */
                writer.write(instance, instance_handle);
                try {
                    Thread.sleep(sendPeriodMillis);
                } catch (InterruptedException ix) {
                    System.err.println("INTERRUPTED");
                    break;
                }
            }

            //writer.unregister_instance(instance, instance_handle);

        } finally {

            // --- Shutdown --- //

            if(participant != null) {
                participant.delete_contained_entities();

                DomainParticipantFactory.TheParticipantFactory.
                delete_participant(participant);
            }
            /* RTI Data Distribution Service provides finalize_instance()
            method for people who want to release memory used by the
            participant factory singleton. Uncomment the following block of
            code for clean destruction of the participant factory
            singleton. */
            //DomainParticipantFactory.finalize_instance();
        }
    }
}
