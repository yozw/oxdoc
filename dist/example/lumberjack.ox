#include <oxstd.h>

/** The Lumberjack class represents a lumber jack **/
class Lumberjack {
   decl wearsWomensClothes;
   isOk();
   sleep(const hours);

   Lumberjack(const wearsWomensClothes);
   ~Lumberjack();
};

/** Construct a new lumber jack
    @param wearsWomensClothes Indicates whether the lumber jack wears women's clothes. **/
Lumberjack::Lumberjack(const wearsWomensClothes) {
   this.wearsWomensClothes = wearsWomensClothes;
   println("A lumber jack is born.");
}

/** Checks whether this lumber jack is okay
   @returns <tt>TRUE</tt> if this lumber jack is okay, <TT>FALSE</TT> if not.
   @comments In the current implementation, lumber jacks are okay if and only
   if they wear women's clothing.
   @see Lumberjack **/
Lumberjack::isOk() {
   if (this.wearsWomensClothes) 
      return TRUE;
   else
      return FALSE;
}

/** Kills the lumber jack **/
Lumberjack::~Lumberjack() {
   println("'E's not pinin'!  'E's passed on!  This lumber jack is no more!  He has\nceased " +
       "to be!  'E's expired and gone to meet 'is maker!  'E's a stiff!\nBereft " +
       "of life, 'e rests in peace!  If you hadn't nailed 'im to the perch\n'e'd be " +
       "pushing up the daisies!  'Is metabolic processes are now 'istory!\n'E's off " +
       "the twig!  'E's kicked the bucket, 'e's shuffled off 'is mortal\ncoil, run " +
       "down the curtain and joined the bleedin' choir invisibile!!\n" +
       "THIS IS AN EX-LUMBER JACK!!");
}

/** Make the lumber jack sleep for a specified number of hours 
   @param hours Number of hours to sleep. Has to be integer.
   @comments The lumber jack cannot sleep more than 24 hours a day. **/
Lumberjack::sleep(const hours) {
   println("The lumber jack is asleep");
   println("(", hours, " hours later...)");
   println("The lumber jack wakes up");
}



/** The main program 
 @comments Gives birth to a lumber jack, have him sleep, checks whether
  the lumber jack is okay, and kills it. **/
main() {
   decl john = new Lumberjack(TRUE);
   john.sleep(7);
   if (john.isOk()) 
      println("This lumber jack is okay.");
   delete john;
}