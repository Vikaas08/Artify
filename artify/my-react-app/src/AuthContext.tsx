import {
  createContext,
  useState,
  useContext,
  useEffect,
  ReactNode,
} from "react";
import Cookies from "js-cookie";
import { jwtDecode } from "jwt-decode";
import axios from "axios";
import { useToast } from "./ToastContext";

interface UserData {
  id: string;
  fullname: string;
  email: string;
  profileImageUrl: string;
}

interface AuthContextType {
  userData: UserData | null;
  login: (data: UserData, token: string) => void;
  logout: () => void;

  bookmarkedIds: Set<string>;
  toggleBookmark: (id: string, img: string) => void;
  bookmarkedDetails: { [key: string]: any };
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth error");
  }
  return context;
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [userData, setUserData] = useState<UserData | null>(null);
  const [bookmarkedIds, setBookmarkedIds] = useState<Set<string>>(new Set());
  const [bookmarkedDetails, setBookmarkedDetails] = useState<{
    [key: string]: any;
  }>({});

  const { addToast } = useToast();

  useEffect(() => {
    const token = Cookies.get("token");
    if (token) {
      try {
        const decoded: any = jwtDecode(token);
        setUserData(decoded);
      } catch (error) {
        console.error("Invalid token:", error);
      }
    }
    const savedBookmarks = localStorage.getItem("bookmarkedIds");
    if (savedBookmarks) {
      setBookmarkedIds(new Set(JSON.parse(savedBookmarks)));
    }
    const savedBookmarkDetails = localStorage.getItem("bookmarkedDetails");
    if (savedBookmarkDetails) {
      setBookmarkedDetails(JSON.parse(savedBookmarkDetails));
    }
  }, []);

  const login = (data: UserData, token: string) => {
    try {
      console.log("Login data:", data);
      const decoded: any = jwtDecode(token);
      setUserData(decoded);
    } catch (error) {
      console.error("Failed to decode token:", error);
    }
    Cookies.set("token", token, { expires: 1 / 24 });
  };

  const logout = () => {
    // console.log("authcontext logout called");
    Cookies.remove("token");
    setUserData(null);
  };

  const toggleBookmark = (id: string, img: string) => {
    setBookmarkedIds((prev) => {
      const newBookmarkedIds = new Set(prev);
      const action = newBookmarkedIds.has(id) ? "remove" : "add";

      if (action === "remove") newBookmarkedIds.delete(id);
      else newBookmarkedIds.add(id);

      localStorage.setItem(
        "bookmarkedIds",
        JSON.stringify(Array.from(newBookmarkedIds))
      );
      return newBookmarkedIds;
    });

    if (userData) {
      if (!bookmarkedIds.has(id)) {
        axios
          .post("https://hw3-cs571-spring-25.uw.r.appspot.com/api/about", {
            uid: id,
          })
          .then((res) => {
            const response = res.data;
            const bookmarkDetails = {
              id: id ?? null,
              fullname: response?.name ?? null,
              dob: response?.birthday ?? null,
              dod: response?.deathday ?? null,
              nationality: response?.nationality ?? null,
              profileImageUrl: img ?? "/artsy_logo.svg",
              timestamp: new Date().toISOString(),
            };

            setBookmarkedDetails((prevDetails) => {
              const updatedDetails = { ...prevDetails, [id]: bookmarkDetails };
              localStorage.setItem(
                "bookmarkedDetails",
                JSON.stringify(updatedDetails)
              );
              return updatedDetails;
            });

            axios
              .post(
                "https://hw3-cs571-spring-25.uw.r.appspot.com/api/favourites",
                {
                  id,
                  fullname: bookmarkDetails.fullname,
                  dob: bookmarkDetails.dob,
                  dod: bookmarkDetails.dod,
                  nationality: bookmarkDetails.nationality,
                  profileImageUrl: bookmarkDetails.profileImageUrl,
                  timestamp: bookmarkDetails.timestamp,
                  action: "add",
                },
                { withCredentials: true }
              )
              .then((res) => {
                console.log("Bookmark added:", res.data);
                addToast("Added to favorites", "success");
              })
              .catch((err) => {
                console.log("Error adding bookmark:", err);
              });

            setBookmarkedIds((prev) => {
              const newBookmarkedIds = new Set(prev);
              newBookmarkedIds.add(id);
              localStorage.setItem(
                "bookmarkedIds",
                JSON.stringify(Array.from(newBookmarkedIds))
              );
              return newBookmarkedIds;
            });
          })
          .catch((err) => {
            console.log("Error while sending bookmark request", err);
          });
      } else {
        axios
          .post(
            "https://hw3-cs571-spring-25.uw.r.appspot.com/api/favourites",
            {
              id,
              action: "remove",
            },
            { withCredentials: true }
          )
          .then((res) => {
            console.log("Bookmark removed:", res.data);
            addToast("Removed from favorites", "danger");
          })
          .catch((err) => {
            console.log("Error removing bookmark:", err);
          });

        setBookmarkedDetails((prevDetails) => {
          const updatedDetails = { ...prevDetails };
          delete updatedDetails[id];
          localStorage.setItem(
            "bookmarkedDetails",
            JSON.stringify(updatedDetails)
          );
          return updatedDetails;
        });
      }
    }
  };

  return (
    <AuthContext.Provider
      value={{
        userData,
        login,
        logout,
        bookmarkedIds,
        toggleBookmark,
        bookmarkedDetails,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};
