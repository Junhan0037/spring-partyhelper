package com.partyhelper.modules.party;

import com.partyhelper.modules.account.domain.Account;
import com.partyhelper.modules.settings.domain.Tag;
import com.partyhelper.modules.settings.domain.Zone;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@NamedEntityGraph(name = "Party.withAll", attributeNodes = {
        @NamedAttributeNode("tags"),
        @NamedAttributeNode("zones"),
        @NamedAttributeNode("managers"),
        @NamedAttributeNode("members")})
@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Party {

    @Id @GeneratedValue
    private Long id;

    @ManyToMany
    private Set<Account> managers = new HashSet<>(); // 고객

    @ManyToMany
    private Set<Account> members = new HashSet<>(); // 업체

    @Column(unique = true)
    private String path;

    private String title;

    private String shortDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String fullDescription;

    @Lob @Basic(fetch = FetchType.EAGER)
    private String image;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();

    private LocalDateTime publishedDateTime;

    private LocalDateTime closedDateTime;

    private LocalDateTime recruitingUpdatedDateTime;

    private boolean recruiting;

    private boolean published;

    private boolean closed;

    private boolean useBanner;

    private int memberCount;

    public void addManager(Account account) {
        this.managers.add(account);
    }

    public boolean isJoinable(Account account) {
        return this.isPublished() && this.isRecruiting() && !isMember(account) && !isManager(account);
    }

    public boolean isMember(Account account) {
        for (Account member : this.members) {
            if (account.getId().equals(member.getId())) {
                return true;
            }
        }
        return false;
    }

    public boolean isManager(Account account) {
        for (Account manager : this.managers) {
            if (account.getId().equals(manager.getId())) {
                return true;
            }
        }
        return false;
    }

}
