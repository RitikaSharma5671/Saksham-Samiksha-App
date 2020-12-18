package com.samagra.grove.logging;

import com.amazonaws.services.simpleemail.model.SendEmailResult;

public interface EmailListener {
        /**
         * The operation succeeded
         * @param s
         */
        void onDone(SendEmailResult s);

}


