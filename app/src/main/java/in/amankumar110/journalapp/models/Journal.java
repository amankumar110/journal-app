package in.amankumar110.journalapp.models;

import com.google.firebase.Timestamp;

    public class Journal {

        private String thoughts, title, imageUrl, userId, userName;
        private Timestamp timeAdded;

        // Default constructor
        public Journal() {
        }

        public Journal(String thoughts, String title, String imageUrl, String userId, String userName, Timestamp timeAdded) {
            this.thoughts = thoughts;
            this.title = title;
            this.imageUrl = imageUrl;
            this.userId = userId;
            this.userName = userName;
            this.timeAdded = timeAdded;
        }

        public String getThoughts() {
            return thoughts;
        }

        public void setThoughts(String thoughts) {
            this.thoughts = thoughts;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public Timestamp getTimeAdded() {
            return timeAdded;
        }

        public void setTimeAdded(Timestamp timeAdded) {
            this.timeAdded = timeAdded;
        }
    }