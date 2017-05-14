package com.example.mingchengzhu.dejaphoto;

/**
 * Created by ruihanzeng on 5/8/17.
 */

public class PreviousImage {

    private DejaPhoto[] previous_photo;
    int index;

    public PreviousImage(){
        previous_photo = new DejaPhoto[10];
        index = 0;
    }

    /**
     * similar to a push
     * adds newest photo to photo deque. If already 10 photos in deque,
     * oldest one is removed
     *
     * @param nextPhoto photo generated on swiping/auto-swiping right
     */
    public void swipeRight(DejaPhoto nextPhoto){
        if(isFull()){
            shiftLeftby1All();
            previous_photo[9] = nextPhoto;
        }else{
            previous_photo[index] = nextPhoto;
            index++;
        }
    }

    /**
     * similar to a pop
     * returns the previous photo
     * note: null is returned if no previous photo is avalible
     *
     * @return the previous photo
     */
    public DejaPhoto swipeLeft(){
        if(isEmpty()){
            return null;
        }else{
            index-=2;
            return previous_photo[index++];
        }
    }
    
    /**
     * similar to a top
     * returns previous image without removing it
     * note: null is returned if no previous photo is avalible
     *
     * @return the previous photo
     */
    public DejaPhoto getLastPhoto(){
        if(isEmpty()){
            return null;
        }else{
            return previous_photo[index - 1];
        }
    }
    

    /**
     * checks if a photo has been previously seen before or not
     *
     * @param photo photo being checked
     * @return true if photo is in the deque and false if not
     */
    public boolean PhotoPreviouslySeen(DejaPhoto photo){
        for(int i = 0; i < index; i++){
            if(photo.equals(previous_photo[i])){
                return true;
            }
        }
        return false;
    }

    /**
     * helper function only do not use outside this class
     */
    private void shiftLeftby1All(){
        for(int i = 0; i < 9; i ++){
            previous_photo[i] = previous_photo[i + 1];
        }
    }

    /**
     * helper function only do not use outside this class
     */
    private boolean isFull(){
        return index == 10;
    }

    /**
     * helper function only do not use outside this class
     */
    private boolean isEmpty(){
        return index == 0;
    }
    
    /**
     * @return the number of images currently being stored
     */
    public int getNumberofPhoto(){
        return index;
    }

    /**
     * Getter For testing
     * @return index
     */
    public int getIndex(){
        return this.index;
    }
}
